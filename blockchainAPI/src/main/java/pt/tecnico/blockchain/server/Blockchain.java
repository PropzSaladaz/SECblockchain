package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.Application;
import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Pair;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void decide(Content msg) {
        BlockchainBlock blockValue = (BlockchainBlock) msg;
        _lastBlock = new Block(_lastBlock, blockValue.getTransactions());
        printBlockchain();
    }

    @Override
    public boolean validatePrePrepareValue(Content value) {
        return validateCommitValue(value, null);
    }

    @Override
    public boolean validateCommitValue(Content value, List<Content> quorum) {
        BlockchainBlock newBlock = (BlockchainBlock) value;
        String predictedHash = getNextPredictedHash(newBlock);
        return newBlock.getHash().equals(predictedHash);
    }

    public String getNextPredictedHash(BlockchainBlock newBlock) {
        try {
            return Block.computeHash(_lastBlock.getBlockHash(),
                    Block.getBytesFrom(newBlock.getTransactions()));
        } catch (IOException | NoSuchAlgorithmException e){
            Logger.logError("Could not generate next predicted hash");
            e.printStackTrace();
            return "ERROR";
        }
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
