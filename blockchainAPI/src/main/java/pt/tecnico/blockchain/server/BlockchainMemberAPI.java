package pt.tecnico.blockchain.server;
import pt.tecnico.blockchain.Crypto;
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

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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
    private PublicKey _publicKey;

    public BlockchainMemberAPI(DatagramSocket socket, Map<Integer,Pair<String,Integer>> clients, ContractI contract, PublicKey publicKey) throws NoSuchAlgorithmException {
        chain = new Blockchain();
        _socket = socket;
        pool = new SynchronizedTransactionPool();
        _clientsPidToInfo = clients;
        _blockChainState = new BlockChainState();
        _publicKey = publicKey;
        _blockChainState.addContract(contract, Crypto.getHashFromKey(_publicKey));
    }

    @Override
    public void decide(Pair<BlockchainBlock,BlockchainBlock> pair, Content message) {
        DecideBlockMessage decideMsg = (DecideBlockMessage) message;
        chain.decide(pair,decideMsg.getContent());
        sendTransactionResultToClient(pair,message);
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

    public Content addTransactionAndGetBlockIfReady(BlockchainTransaction transaction) {
        List<BlockchainTransaction> transactions;
        pool.addTransactionIfNotInPool(transaction);
        if ((transactions = pool.getTransactionsIfHasEnough()).size() > 0) {
            return new BlockchainBlock(transactions);
        }
        return null;
    }

    public void sendTransactionResultToClient(Pair<BlockchainBlock,BlockchainBlock> pair,Content content) {
        try {
            List<BlockchainTransaction> completeTransactions = pair.getFirst().getTransactions();
            List<BlockchainTransaction> validTransactions = pair.getSecond().getTransactions();
           for (BlockchainTransaction transaction : completeTransactions ){
               DecideBlockMessage finalMessage = (DecideBlockMessage) content;
               finalMessage.setContent(transaction);
               if (validTransactions.contains(transaction)){
                 finalMessage.setStatus(DecideBlockMessage.SUCCESSFUL_TRANSACTION);
               }else{finalMessage.setStatus(DecideBlockMessage.REJECTED_TRANSACTION);}
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

    public Content validateTransactions(Content content){
        BlockchainBlock block = (BlockchainBlock) content;
        BlockchainBlock newBlock= new BlockchainBlock();
        List<BlockchainTransaction> transactions = block.getTransactions();
        for (BlockchainTransaction transaction : transactions){
             if(_blockChainState.getContract(transactions.get(0).getContractID()).validateBlock(
                     transaction.getContent())){
                 _blockChainState.getContract(transactions.get(0).getContractID()).checkBalance(transaction.getContent());
                 newBlock.addTransaction(transaction);
             }
        }
        return newBlock;
    }
}