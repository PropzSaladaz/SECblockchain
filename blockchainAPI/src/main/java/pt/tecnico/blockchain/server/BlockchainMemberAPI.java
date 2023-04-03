package pt.tecnico.blockchain.server;
import pt.tecnico.blockchain.*;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.TransactionResultMessage;
import pt.tecnico.blockchain.Messages.tes.TESTransaction;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.contracts.SmartContract;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.net.DatagramSocket;

import pt.tecnico.blockchain.Pair;

public class BlockchainMemberAPI implements Application {
    private Blockchain chain;
    private SynchronizedTransactionPool pool;
    private DatagramSocket _socket;
    private Map<Integer,Pair<String,Integer>> _clientsPidToInfo;
    private BlockChainState _blockChainState;
    private PublicKey _publicKey;
    private Boolean _isMiner;

    public BlockchainMemberAPI(DatagramSocket socket, Map<Integer,Pair<String,Integer>> clients, PublicKey publicKey) throws NoSuchAlgorithmException {
        chain = new Blockchain();
        _socket = socket;
        pool = new SynchronizedTransactionPool();
        _clientsPidToInfo = clients;
        _blockChainState = new BlockChainState();
        _publicKey = publicKey;
    }

    @Override
    public void decide(Content msg) {
        chain.decide(msg);
        sendTransactionResultToClient(msg);
    }

    @Override
    public void setMiner(Boolean isMiner){_isMiner = isMiner;}

    @Override
    public boolean validateValue(Content value) {
        return chain.validateValue(value);
    }

    @Override
    public int getNextInstanceNumber() {
        return chain.getNextInstanceNumber();
    }

    @Override
    public void prepareValue(Content value) {
        chain.prepareValue(value);
    }

    public Content addTransactionAndGetBlockIfReady(BlockchainTransaction transaction) {
        List<BlockchainTransaction> transactions;
        pool.addTransactionIfNotInPool(transaction);
        if ((transactions = pool.getTransactionsIfHasEnough()).size() > 0) {
            return new BlockchainBlock(transactions);
        }
        return null;
    }

    public void executeStrongRead(BlockchainTransaction transaction) throws Exception {
        TransactionResultMessage finalMessage = new TransactionResultMessage(transaction);
        if(_blockChainState.existContract(transaction.getContractID())){
            if(_blockChainState.getContract(transaction.getContractID()).assertTransaction(transaction.getContent())){
                finalMessage.setStatus(TransactionResultMessage.SUCCESSFUL_TRANSACTION);
            }else{
                finalMessage.setStatus(TransactionResultMessage.REJECTED_TRANSACTION);
            }
        }else{
            finalMessage.setStatus(TransactionResultMessage.REJECTED_TRANSACTION);
        }

        Pair<String,Integer> senderInfo = _clientsPidToInfo.get(RSAKeyStoreById.getPidFromPublic(KeyConverter.base64ToPublicKey(transaction.getSender())));
        AuthenticatedPerfectLink.send(_socket, finalMessage, senderInfo.getFirst(), senderInfo.getSecond());
    }

    public void sendTransactionResultToClient(Content content) {
        try {
            BlockchainBlock block = (BlockchainBlock) content;
            List<BlockchainTransaction> transactions = block.getTransactions();
            for (BlockchainTransaction transaction : transactions ) {
                TransactionResultMessage finalMessage = new TransactionResultMessage(transaction);
                if (transaction.getStatus().equals(BlockchainTransaction.APPENDED)){
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

    public void validateBlockTransactions(Content content){
        BlockchainBlock block = (BlockchainBlock) content;
        List<BlockchainTransaction> transactions = block.getTransactions();
        for (BlockchainTransaction transaction : transactions) {
            if(_blockChainState.existContract(transaction.getContractID())){
                if (_blockChainState.getContract(transaction.getContractID()).assertTransaction(transaction.getContent())){
                    transaction.setStatus(BlockchainTransaction.APPENDED);
                }
            }
        }
    }
}