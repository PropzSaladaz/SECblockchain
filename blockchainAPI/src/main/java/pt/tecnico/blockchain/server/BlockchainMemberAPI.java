package pt.tecnico.blockchain.server;
import pt.tecnico.blockchain.*;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.TransactionResultMessage;
import pt.tecnico.blockchain.Messages.ibft.SignedBlockchainBlockMessage;
import pt.tecnico.blockchain.Messages.tes.TESTransaction;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.contracts.SmartContract;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.net.DatagramSocket;

import pt.tecnico.blockchain.Pair;

public class BlockchainMemberAPI implements Application {
    private Blockchain chain;
    private SynchronizedTransactionPool pool;
    private DatagramSocket _socket;
    private Map<Integer,Pair<String,Integer>> _clientsPidToInfo;
    private BlockChainState _blockChainState;
    private String _publicKey;

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

    public Content addTransactionAndGetBlockIfReady(BlockchainTransaction transaction) {
        List<BlockchainTransaction> transactions;
        pool.addTransactionIfNotInPool(transaction);
        if ((transactions = pool.getTransactionsIfHasEnough()).size() > 0) {
            return new BlockchainBlock(transactions);
        }
        return null;
    }

    public void executeRead(BlockchainTransaction requestTx) throws Exception {
        SmartContract contract = _blockChainState.getContract(requestTx.getContractID());
        TransactionResultMessage txResult = new TransactionResultMessage(requestTx.getNonce(), null);

        if (contract == null || !contract.assertTransaction(requestTx.getContent(), _publicKey)) {
            txResult.setStatus(TransactionResultMessage.REJECTED_TRANSACTION);
        } else {
            Content resultContent = contract.executeReadTransaction(requestTx.getContent());
            txResult.setContent(resultContent);
            txResult.setStatus(TransactionResultMessage.SUCCESSFUL_TRANSACTION);
        }
     
        Pair<String,Integer> senderInfo = _clientsPidToInfo.get(
            RSAKeyStoreById.getPidFromPublic(KeyConverter.base64ToPublicKey(requestTx.getSender())));
        AuthenticatedPerfectLink.send(_socket, txResult, senderInfo.getFirst(), senderInfo.getSecond());
    }

    public void sendTransactionResultToClient(Content content, List<SignedBlockchainBlockMessage> signaturesQuorum) {
        try {
            BlockchainBlock block = (BlockchainBlock) content;
            List<BlockchainTransaction> transactions = block.getTransactions();
            for (BlockchainTransaction transaction : transactions) {
                TransactionResultMessage finalMessage = new TransactionResultMessage(transaction.getNonce(), transaction);
                if (transaction.getStatus().equals(BlockchainTransaction.APPENDED)){
                    // devia correr aqui acho eu
                    finalMessage.setStatus(TransactionResultMessage.SUCCESSFUL_TRANSACTION);
                } else{
                    finalMessage.setStatus(TransactionResultMessage.REJECTED_TRANSACTION);
                }
                Pair<String,Integer> senderInfo = _clientsPidToInfo.get(RSAKeyStoreById.getPidFromPublic(KeyConverter.base64ToPublicKey(transaction.getSender())));
                AuthenticatedPerfectLink.send(_socket, finalMessage, senderInfo.getFirst(), senderInfo.getSecond());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Blockchain getChain() {
        return chain;
    }

    public SynchronizedTransactionPool getPool() {
        return pool;
    }

    public DatagramSocket getSocket() {
        return _socket;
    }

    public BlockChainState getBlockChainState() {
        return _blockChainState;
    }

    public void addContractToBlockchain(SmartContract _contract) {
        _blockChainState.addContract(_contract);
    }

    public void validateBlockTransactions(BlockchainBlock block) {
        List<BlockchainTransaction> transactions = block.getTransactions();
        for (BlockchainTransaction transaction : transactions) {
            SmartContract contract = _blockChainState.getContract(transaction.getContractID());
            if (contract == null) continue;
            // TODO: Change to enum
            transaction.setStatus(
                contract.assertTransaction(transaction.getContent(), _publicKey) ? 
                BlockchainTransaction.APPENDED :
                "REJECTED"
            );
        }
    }
}