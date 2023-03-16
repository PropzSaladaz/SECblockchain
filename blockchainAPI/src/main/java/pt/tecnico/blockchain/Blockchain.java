package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Blockchain implements Application {

    private Block _lastBlock;

    public Blockchain() {
        _lastBlock = new Block("Im the first Block");
    }


    public int getNextBlockNumber() {
        return _lastBlock.getBlockNumber() + 1;
    }

    public String getLastBlockHash() {
        return _lastBlock.getBlockHash();
    }

    public Block getLastBlock() {
        return _lastBlock;
    }

    public void printBlockchain() {
        Block b = _lastBlock;
        System.out.println("New block appended: ");
        while (b != null) {
            System.out.print(b.toString());
            b = b.getPreviousBlock();
        }
    }

    @Override
    public void decide(Content value, List<Integer> quorum) {
        BlockchainMessage message = (BlockchainMessage) value;
        Block oldLast = _lastBlock;
        _lastBlock = new Block(oldLast, message.getMessage());
        printBlockchain();
    }

    @Override
    public boolean validateValue(Content value) {
        try {
            BlockchainMessage newBlock = (BlockchainMessage) value;
            String predictedHash = Block.computeHash(_lastBlock, newBlock.getMessage());
            return newBlock.getHash().equals(predictedHash);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getNextInstanceNumber() {
        return getNextBlockNumber();
    }

    @Override
    public void prepareValue(Content value) {
        try {
            BlockchainMessage msg = (BlockchainMessage) value;
            msg.setHash(Block.computeHash(_lastBlock, msg.getMessage()));
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
