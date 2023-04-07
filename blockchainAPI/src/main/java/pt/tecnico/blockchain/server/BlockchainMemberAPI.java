package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.*;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.QuorumSignedBlockMessage;
import pt.tecnico.blockchain.Messages.blockchain.TransactionResultMessage;
import pt.tecnico.blockchain.Messages.ibft.SignedBlockchainBlockMessage;
import pt.tecnico.blockchain.contracts.SmartContract;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;

import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus.*;

public class BlockchainMemberAPI implements Application {
    private final Blockchain chain;
    private final SynchronizedTransactionPool pool;
    private final DatagramSocket _socket;
    private final Map<Integer,Pair<String,Integer>> _clientsPidToInfo;
    private final BlockChainState _blockChainState;
    private final String _publicKey;

    public BlockchainMemberAPI(DatagramSocket socket, Map<Integer,Pair<String,Integer>> clients, PublicKey publicKey) {
        chain = new Blockchain();
        _socket = socket;
        pool = new SynchronizedTransactionPool();
        _clientsPidToInfo = clients;
        _blockChainState = new BlockChainState();
        _publicKey = Crypto.base64(publicKey.getEncoded());
    }

    @Override
    public void decide(Content msg) {
        try {
            SignedBlockchainBlockMessage m = (SignedBlockchainBlockMessage) msg;
            chain.decide(m.getContent()); // block is appended with all transactions (VALIDATED and NOT-VALIDATED)
            sendTransactionResultToClient(m.getContent());
        } catch (ClassCastException e) {
            Logger.logWarning("Unexpected message type", e);
        }
    }

    @Override
    public boolean validatePrePrepareValue(Content value) {
        try {
            SignedBlockchainBlockMessage signedValue = (SignedBlockchainBlockMessage) value;
            return chain.validatePrePrepareValue(signedValue.getContent());
        } catch (ClassCastException e) {
            Logger.logWarning("Unexpected message type", e);
            return false;
        }
    }

    @Override
    public boolean validateCommitValue(Content value, List<Content> quorum) {
        try {
            SignedBlockchainBlockMessage signedValue = (SignedBlockchainBlockMessage) value;
            List<Pair<Integer, byte[]>> signaturesQuorum = quorum.stream().map(
                    signedBlockMessage -> new Pair<>(
                            ((SignedBlockchainBlockMessage) signedBlockMessage).getSignerPID(),
                            ((SignedBlockchainBlockMessage) signedBlockMessage).getSignature()
                    )
            ).collect(Collectors.toList());
            validateAndExecuteBlockTransactions(new QuorumSignedBlockMessage(signedValue.getContent(), signaturesQuorum));
            boolean v = chain.validateCommitValue(signedValue.getContent(), quorum);
            Logger.logDebugPrimary("Value validation result: " + v);
            return v;

        } catch (ClassCastException e) {
            Logger.logWarning("Unexpected message type", e);
            return false;
        }

    }

    @Override
    public int getNextInstanceNumber() {
        return chain.getNextInstanceNumber();
    }

    @Override
    public void prepareValue(Content value) {
        try {
            SignedBlockchainBlockMessage signedBlock = (SignedBlockchainBlockMessage) value;
            chain.prepareValue(signedBlock.getContent());
        } catch (ClassCastException e) {
            Logger.logWarning("Unexpected message type", e);
        }
    }


    public void addContractToBlockchain(SmartContract _contract) {
        _blockChainState.addContract(_contract);
    }

    public void validateAndExecuteBlockTransactions(Content content) {
        try {
            QuorumSignedBlockMessage signedBlock = (QuorumSignedBlockMessage) content;
            BlockchainBlock block = (BlockchainBlock) signedBlock.getContent();
            List<BlockchainTransaction> transactions = block.getTransactions();
            for (BlockchainTransaction transaction : transactions) {
                String contractId = transaction.getContractID();
                SmartContract contract = _blockChainState.getContract(contractId);
                transaction.setStatus(
                        (contract != null && contract.validateAndExecuteTransaction(transaction.getContent(), _publicKey, signedBlock)) ?
                                VALIDATED : REJECTED
                );
            }
        } catch (ClassCastException e) {
            Logger.logWarning("Unexpected message type", e);
        }
    }


    /* -------------------------------------------
     *          SEND RESPONSE MESSAGES
     * ---------------------------------------- */

    public void sendTransactionResultToClient(Content content) {
        try {
            BlockchainBlock block = (BlockchainBlock) content;
            List<BlockchainTransaction> transactions = block.getTransactions();
            for (BlockchainTransaction transaction : transactions ) {
                SmartContract contract = _blockChainState.getContract(transaction.getContractID());
                Content contractResp = contract.getTransactionResponse(transaction.getContent(), _publicKey);
                TransactionResultMessage response = new TransactionResultMessage(
                        transaction.getNonce(),
                        contractResp
                );
                sendResponseToClient(transaction.getSender(), response);
            }
        } catch (Exception e) {
            Logger.logError("", e);
        }
    }

    private void sendResponseToClient(String sender, TransactionResultMessage response) throws Exception {
        Integer clientPid = RSAKeyStoreById.getPidFromPublic(sender);
        Logger.logInfo("clientPID = " + clientPid);
        Pair<String,Integer> senderInfo = _clientsPidToInfo.get(clientPid);
        AuthenticatedPerfectLink.send(_socket, response, senderInfo.getFirst(), senderInfo.getSecond());
    }


    /* -------------------------------------------
     *         HANDLE RECEIVED MESSAGES
     * ---------------------------------------- */

    public void parseTransaction(BlockchainTransaction transaction) throws Exception {
        switch (transaction.getOperationType()) {
            case UPDATE:
                BlockchainBlock block = addTransactionAndGetBlockIfReady(transaction);
                if (block != null) {
                    validateTransactionsFromBlock(block);
                    block.setHash(chain.getNextPredictedHash(block));
                    Ibft.start(new SignedBlockchainBlockMessage(block));
                }
                break;
            case READ:
                parseRead(transaction);
                break;
            default:
                break;
        }
    }

    /**
     * Validates txns and sets current state on the txns (invokes updateTransactionWithCurrentState from
     * the contract)
     */
    public void validateTransactionsFromBlock(BlockchainBlock block) {
        for (BlockchainTransaction transaction : block.getTransactions()) {
            String contractId = transaction.getContractID();
            SmartContract contract = _blockChainState.getContract(contractId);
            if (contract != null) {
                transaction.setStatus( contract.validateTransaction(transaction.getContent()) ? VALIDATED : REJECTED);
                contract.updateTransactionWithCurrentState(transaction.getContent());
            } else transaction.setStatus(REJECTED);
        }
    }

    private BlockchainBlock addTransactionAndGetBlockIfReady(BlockchainTransaction transaction) {
        List<BlockchainTransaction> transactions;
        pool.addTransactionIfNotInPool(transaction);
        if ((transactions = pool.getTransactionsIfHasEnough()).size() > 0) {
            return new BlockchainBlock(transactions);
        }
        return null;
    }

    private void parseRead(BlockchainTransaction transaction) throws Exception {
        TransactionResultMessage response = new TransactionResultMessage(transaction.getNonce(), null);
        String contractID = transaction.getContractID();
        if(_blockChainState.existContract(contractID)) {
            SmartContract contract = _blockChainState.getContract(contractID);
            // In reads we request a transaction proof instead of providing one
            if (contract.validateAndExecuteTransaction(transaction.getContent(), _publicKey, null)) {
                response.setStatus(VALIDATED);
                // This content corresponds to the SignedQuorumAndBlockMessage (balance proof)
                Content resultContent = contract.getTransactionResponse(transaction.getContent(), _publicKey);
                response.setContent(resultContent);
            }
            else response.setStatus(REJECTED, "Transaction could not be validated.");
        } else response.setStatus(REJECTED, "Contract with id '" + transaction.getContractID() + "' doesn't exist.");
        sendResponseToClient(transaction.getSender(), response);
    }
}