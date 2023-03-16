package pt.tecnico.blockchain;

import java.security.NoSuchAlgorithmException;
import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;

public class Block {
    private Block _previousBlock;
    private String _hash;
    private int _blockNumber;

    public Block(String initialMessage)  {
        _hash = initialMessage;
        _blockNumber = 0;
    }
    public Block(Block previousBlock, BlockchainMessage value) throws NoSuchAlgorithmException {
        _previousBlock = previousBlock;
        _hash = Crypto.computeHash(value.getMessage(),_previousBlock.getBlockHash());
        _blockNumber = _previousBlock.getBlockNumber()+1;
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
}
