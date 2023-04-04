package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.*;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.TransactionResultMessage;
import pt.tecnico.blockchain.Messages.ibft.SignedBlockchainBlockMessage;
import pt.tecnico.blockchain.contracts.SmartContract;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;

import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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

    public BlockchainMemberAPI(DatagramSocket socket, Map<Integer,Pair<String,Integer>> clients, PublicKey publicKey) throws NoSuchAlgorithmException {
        chain = new Blockchain();
        _socket = socket;
        pool = new SynchronizedTransactionPool();
        _clientsPidToInfo = clients;
        _blockChainState = new BlockChainState();
        _publicKey = Crypto.getHashFromKey(publicKey);
    }

    @Override
    public void decide(Content msg, List<Content> quorum) {
        List<SignedBlockchainBlockMessage> blockSignaturesQuorum = quorum.stream().map(
            content -> {
                return (SignedBlockchainBlockMessage) content;
            }
        ).collect(Collectors.toList());
        chain.decide(msg, null);
        sendTransactionResultToClient(msg, blockSignaturesQuorum);
    }

    @Override
    public boolean validateValue(Content value) {
        SignedBlockchainBlockMessage signedValue = (SignedBlockchainBlockMessage) value;
        validateBlockTransactions((BlockchainBlock)signedValue.getContent());
        return chain.validateValue(signedValue.getContent());
    }

    @Override
    public int getNextInstanceNumber() {
        return chain.getNextInstanceNumber();
    }

    @Override
    public void prepareValue(Content value) {
        SignedBlockchainBlockMessage signedBlock = (SignedBlockchainBlockMessage) value;
        chain.prepareValue(signedBlock.getContent());
    }


    public void addContractToBlockchain(SmartContract _contract) {
        _blockChainState.addContract(_contract);
    }

    public void validateBlockTransactions(Content content){
        BlockchainBlock block = (BlockchainBlock) content;
        List<BlockchainTransaction> transactions = block.getTransactions();
        for (BlockchainTransaction transaction : transactions) {
            String contractId = transaction.getContractID();
            if(_blockChainState.existContract(contractId)){
                SmartContract contract = _blockChainState.getContract(contractId);
                if (contract.assertTransaction(transaction.getContent(), _publicKey))
                    transaction.setStatus(APPENDED);
            }
        } // TODO set REJECTED
    }


    /* -------------------------------------------
     *          SEND RESPONSE MESSAGES
     * ---------------------------------------- */

    public void sendTransactionResultToClient(Content content, List<SignedBlockchainBlockMessage> signaturesQuorum) {
        try {
            BlockchainBlock block = (BlockchainBlock) content;
            List<BlockchainTransaction> transactions = block.getTransactions();
            for (BlockchainTransaction transaction : transactions ) {
                TransactionResultMessage response = new TransactionResultMessage(transaction.getNonce(), transaction);
                if (transaction.getStatus() == APPENDED){
                    response.setStatus(SUCCESS);
                    // TODO
                }
                else response.setStatus(FAILURE, "Transaction could not be validated.");
                sendResponseToClient(transaction.getSender(), response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendResponseToClient(String sender, TransactionResultMessage response) throws Exception {
        PublicKey clientkey = KeyConverter.base64ToPublicKey(sender);
        Integer clientPid = RSAKeyStoreById.getPidFromPublic(clientkey);
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
                if (block != null) Ibft.start(block);
                break;
            case READ:
                Thread worker = new Thread(() -> {
                    try {
                        parseRead(transaction);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                worker.start();
                break;
            default:
                break;
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
            if (contract.assertTransaction(transaction.getContent(), _publicKey)) {
                Content resultContent = contract.executeReadTransaction(transaction.getContent());
                response.setContent(resultContent);
                response.setStatus(SUCCESS);
            }
            else response.setStatus(FAILURE, "Transaction could not be validated.");
        } else response.setStatus(FAILURE, "Contract with id '" + transaction.getContractID() + "' doesn't exist.");
        sendResponseToClient(transaction.getSender(), response);
    }
}