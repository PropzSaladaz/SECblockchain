package pt.tecnico.blockchain;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

public class Blockchain {

    private Block _lastBlock;

    public Blockchain() throws NoSuchAlgorithmException {
        _lastBlock = new Block("0", "Im the first Block");
    }

    public void addBlock(BlockchainMessage value) throws NoSuchAlgorithmException {
        _lastBlock = new Block(_blockChain.getLast(), value.getMessage());
    }

    public Integer getNextBlockNumber() {
        return ++_lastBlock.getBlockNumber();
    }

    public Block getLastBlock() {
        return _lastBlock;
    }
}
