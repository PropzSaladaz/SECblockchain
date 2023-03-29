package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Blockchain implements Application {

    private Block _lastBlock;

    public Blockchain() {
        _lastBlock = new Block("Im the first Block");
    }


    public String getLastBlockHash() {
        return _lastBlock.getBlockHash();
    }

    public Block getLastBlock() {
        return _lastBlock;
    }

    public void printBlockchain() {
        Block b = _lastBlock;
        StringBuilder s = new StringBuilder("New block appended: ");
        while (b != null) {
            s.append(b);
            b = b.getPreviousBlock();
        }
        Logger.logInfo(s.toString());
    }

    @Override
    public void decide(Content value) {
        BlockchainMessage blockValue = (BlockchainMessage) value;
        _lastBlock = new Block(_lastBlock, blockValue.getMessage());
        printBlockchain();
    }

    @Override
    public boolean validateValue(Content value) {
        try {
            BlockchainMessage newBlock = (BlockchainMessage) value;
            String predictedHash = Block.computeHash(_lastBlock.getBlockHash(), newBlock.getMessage());
            return newBlock.getHash().equals(predictedHash);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getNextInstanceNumber() {
        return _lastBlock.getBlockNumber() + 1;
    }

    @Override
    public void prepareValue(Content value) {
        try {
            BlockchainMessage block = (BlockchainMessage) value;
            block.setHash(Block.computeHash(_lastBlock.getBlockHash(), block.getMessage()));
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
