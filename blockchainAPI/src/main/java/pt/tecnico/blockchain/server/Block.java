package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Block implements Serializable {
    private Block _previousBlock;
    private String _hash;
    private List<BlockchainTransaction> _transactions;
    private int _blockNumber;

    public Block(List<BlockchainTransaction> transactions)  {
        _transactions = transactions;
        _blockNumber = 0;
        _hash = Crypto.base64("GENESIS_BLOCK".getBytes());
    }

    public Block(Block previousBlock, List<BlockchainTransaction> transactions) {
        try {
            _previousBlock = previousBlock;
            _hash = computeHash(previousBlock.getBlockHash(), getBytesFrom(transactions));
            _blockNumber = _previousBlock.getBlockNumber()+1;
            _transactions = transactions;
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public List<BlockchainTransaction> getTransactions() {
        return _transactions;
    }

    public int getBlockNumber() {
        return _blockNumber;
    }

    public Block getPreviousBlock() {
        return _previousBlock;
    }

    public String getBlockHash() {
        return _hash;
    }

    public static String computeHash(String previousBlockHash, byte[] transactions)
            throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = Crypto.getDigest();
        digest.update(transactions);
        digest.update(previousBlockHash.getBytes());
        return Crypto.base64(digest.digest());
    }

    public static byte[] getBytesFrom(Object obj) throws IOException {
        ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(bytesOS);
        objectOS.writeObject(obj);
        return bytesOS.toByteArray();
    }

    @Override
    public String toString() {
        String hash = (_hash == null) ? "" :  _hash.substring(0,15) + "...";
        return " -> [Transactions: " + _transactions + "  , Hash: " +  hash + "]";
    }
}
