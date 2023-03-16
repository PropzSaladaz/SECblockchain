package pt.tecnico.blockchain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;

public class Block {
    private Block _previousBlock;
    private String _hash;
    private String _message;
    private int _blockNumber;

    public Block(String initialMessage)  {
        _hash = initialMessage;
        _blockNumber = 0;
    }
    public Block(Block previousBlock, String message) {
        try {
            _previousBlock = previousBlock;
            _hash = computeHash(previousBlock, message);
            _blockNumber = _previousBlock.getBlockNumber()+1;
            _message = message;
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

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

    public static String computeHash(Block previousBlock, String message) throws IOException, NoSuchAlgorithmException {
        byte[] blockBytes = getBytesFrom(previousBlock);
        byte[] messageBytes = getBytesFrom(message);
        ByteBuffer buffer = ByteBuffer.allocate(blockBytes.length + messageBytes.length);
        buffer.put(blockBytes);
        buffer.put(messageBytes);
        byte[] digest = Crypto.digest(buffer.array());
        return Arrays.toString(Base64.getEncoder().encode(digest));
    }

    private static byte[] getBytesFrom(Object obj) throws IOException {
        ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(bytesOS);
        objectOS.writeObject(obj);
        return bytesOS.toByteArray();
    }
}
