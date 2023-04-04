package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.Application;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Pair;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Blockchain implements Application {

    private Block _lastBlock;

    public Blockchain() {
        _lastBlock = new Block(new ArrayList<>());
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
    public void decide(Content msg, List<Content> quorum) {
        BlockchainBlock blockValue = (BlockchainBlock) msg;
        _lastBlock = new Block(_lastBlock, blockValue.getTransactions());
        printBlockchain();
    }

    @Override
    public boolean validateValue(Content value) {
        try {
            BlockchainBlock newBlock = (BlockchainBlock) value;
            String predictedHash = Block.computeHash(_lastBlock.getBlockHash(),
                    Block.getBytesFrom(newBlock.getTransactions()));
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
            BlockchainBlock block = (BlockchainBlock) value;
            block.setHash(Block.computeHash(_lastBlock.getBlockHash(),
                    Block.getBytesFrom(block.getTransactions())));
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
