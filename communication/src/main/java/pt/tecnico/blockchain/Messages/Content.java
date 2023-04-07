package pt.tecnico.blockchain.Messages;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Logger;


public interface Content extends Serializable {
    
    String toString(int tabs);

    boolean equals(Content another);

    default byte[] digestMessageFields() {
        try {
            return MessageManager.getContentBytes(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    default void sign(Integer signerPID) {}


    // TODO should not receive the PID, but the key!
    default boolean verifySignature(Integer signerPID, byte[] signature) {
        try {
            return Crypto.verifySignature(digestMessageFields(), signature, RSAKeyStoreById.getPublicFromPid(signerPID));
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    default String toStringWithTabs(String str, int numTabs) {
        String tabs = "\t".repeat(numTabs);
        return tabs + str + "\n";
    }

    default String toHash() {
        try {
            return Crypto.base64(Crypto.digest(MessageManager.getContentBytes(this)));
        } catch (IOException | NoSuchAlgorithmException e) {
            Logger.logError("Could not create hash for content.", e);
            throw new RuntimeException();
        }
    }
}


