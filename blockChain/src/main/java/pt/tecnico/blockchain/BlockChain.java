package pt.tecnico.blockchain;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

public class BlockChain {
    private LinkedList<Block> _blockChain;

    public BlockChain() throws NoSuchAlgorithmException {
        _blockChain = new LinkedList<>();
        _blockChain.add(createGenesisBlock());
    }

    public void addBlock(String message) throws NoSuchAlgorithmException {
        Block previousBlock = _blockChain.getLast();
        String previousHash = previousBlock.getHash();
        Block block = new Block(previousHash, message);
        _blockChain.add(block);
    }

    private Block createGenesisBlock() throws NoSuchAlgorithmException {
        return new Block("0", "Im the first Block");
    }

    public LinkedList<Block> getBlockChain() {
        return _blockChain;
    }

}
