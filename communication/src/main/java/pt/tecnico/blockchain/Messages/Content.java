package pt.tecnico.blockchain.Messages;

import java.io.Serializable;
import java.security.PublicKey;
import java.security.PrivateKey;
import pt.tecnico.blockchain.Crypto;


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

    default boolean verifySignature(PublicKey pubKey, byte[] signature) {
        try {
            return Crypto.verifySignature(digestMessageFields(), signature, pubKey);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    default String toStringWithTabs(String str, int numTabs) {
        String tabs = "\t".repeat(numTabs);
        return tabs + str + "\n";
    }
}


