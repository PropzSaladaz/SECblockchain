package pt.tecnico.blockchain;

import java.security.NoSuchAlgorithmException;
import pt.tecnico.blockchain.Crypto;

public class Block {
    private Block _previousBlock;
    private String _hash;
    private Integer _blockNumber;

    public Block(Block previousBlock, BlockchainMessage value) throws NoSuchAlgorithmException {
        _previousBlock = previousBlock;
        _hash = Crypto.computeHash(value.getMessage());
        _blockNumber = _previousBlock.getBlockNumber()++;
    }

    public void getBlockNumber() {
        return _blockNumber;
    }

    public String getPreviousBlock() {
        return _previousBlock;
    }

    public String getBlockHash() {
        return _hash;
    }
}
