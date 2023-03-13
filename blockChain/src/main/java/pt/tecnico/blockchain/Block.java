package pt.tecnico.blockchain;

import java.security.NoSuchAlgorithmException;
import pt.tecnico.blockchain.Crypto;

public class Block {
    private String _previousHash;
    private String _hash;
    private Integer _id;
    private static Integer _generalId = 0;

    public Block(String previousHash, String message) throws NoSuchAlgorithmException {
        _previousHash = previousHash;
        _hash = Crypto.computeHash(message);
        _id = _generalId++;

    }
    public String getPreviousHash() {
        return _previousHash;
    }

    public String getHash() {
        return _hash;
    }

    public Integer getId() {
        return _id;
    }
}
