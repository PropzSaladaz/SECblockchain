package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.Application;
import pt.tecnico.blockchain.KeyConverter;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;
import pt.tecnico.blockchain.Messages.tes.TESTransaction;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;

public class BlockchainMemberAPI implements Application {
    private Blockchain chain;
    private SynchronizedTransactionPool pool;
    private DatagramSocket _socket;
    private Map<Integer,Pair<String,Integer>> _clientsPidToInfo;
    private BlockChainState _blockChainState;

    public BlockchainMemberAPI(DatagramSocket socket, Map<Integer,Pair<String,Integer>> clients,ContractI contract) {
        chain = new Blockchain();
        _socket = socket;
        pool = new SynchronizedTransactionPool();
        _clientsPidToInfo = clients;
        _blockChainState = new BlockChainState();
        _blockChainState.addContract(contract);
    }

    @Override
    public void decide(Content message) {
        DecideBlockMessage decideMsg = (DecideBlockMessage) message;
        chain.decide(decideMsg.getContent());
        sendTransactionResultToClient(decideMsg);
    }

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

    public void addTransactionToPool(BlockchainTransaction transaction) {
        pool.addTransactionIfNotInPool(transaction);
    }

    public void sendTransactionResultToClient(Content message) {
        try {
//            System.out.println("Sending transaction result to client");
            BlockchainBlock block = (BlockchainBlock) message;
            for(BlockchainTransaction transaction : block.getTransactions()){
                TESTransaction tx = (TESTransaction) transaction.getContent();
                Pair<String,Integer> senderInfo = _clientsPidToInfo.get(RSAKeyStoreById.getPidFromPublic(KeyConverter.base64ToPublicKey(tx.getSender())));
                AuthenticatedPerfectLink.send(_socket, message, senderInfo.getFirst(), senderInfo.getSecond());
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

    public Content validateTransactions(Content content){
        BlockchainBlock block = (BlockchainBlock) content;
        BlockchainBlock newBlock= new BlockchainBlock();
        List<BlockchainTransaction> transactions = block.getTransactions();
        for (BlockchainTransaction transaction : transactions){
             if(_blockChainState.getContract(transactions.get(0).getContractID()).validateBlock(
                     (TESTransaction) transaction.getContent())) newBlock.addTransaction(transaction);
        }
        return newBlock;
    }
}