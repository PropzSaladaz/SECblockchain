package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;

import java.security.NoSuchAlgorithmException;

public class Blockchain {

    private Block _lastBlock;

    public Blockchain() throws NoSuchAlgorithmException {
        _lastBlock = new Block("Im the first Block");
    }
    public void addBlock(BlockchainMessage blockMessage) throws NoSuchAlgorithmException {
        Block oldLast = _lastBlock;
        _lastBlock = new Block(oldLast, blockMessage);
    }

    public int getNextBlockNumber() {
        return _lastBlock.getBlockNumber()+1;
    }

    public String getLastBlockHash() {
        return _lastBlock.getBlockHash();
    }

    public boolean verifyChainUpdated(String message, String receivedDigest) throws NoSuchAlgorithmException {
        return (receivedDigest.equals(Crypto.computeHash(message,getLastBlockHash())));

    }
}
