package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.UuidGenerator;

import java.security.*;
import java.util.UUID;

public abstract class TESTransaction implements Content {
    public static String CREATE_ACCOUNT = "C";
    public static String TRANSFER = "T";
    public static String CHECK_BALANCE = "B";

    private String type;
    private String publicKeyHash;
    private String signature;
    private int gasPrice;
    private int gasLimit;
    private UUID id;

    public TESTransaction(String type, String publicKeyHash, int gasPrice, int gasLimit) {
        this.type = type;
        this.publicKeyHash = publicKeyHash;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        id = UuidGenerator.generateUuid();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPublicKeyHash() {
        return publicKeyHash;
    }

    public void setPublicKeyHash(String publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
    }

    public String getSignature() {
        return signature;
    }

    public UUID getId() {
        return id;
    }

    public int getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(int gasPrice) {
        this.gasPrice = gasPrice;
    }

    public int getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(int gasLimit) {
        this.gasLimit = gasLimit;
    }

    public void sign(PrivateKey key)  {
        try {
            Signature signature = Crypto.getSignatureInstance(key);
            signature.update(Byte.parseByte(type));
            signature.update(Byte.parseByte(publicKeyHash));
            signature.update(Byte.parseByte(id.toString()));
            signConcreteAttributes(signature);
            this.signature = Crypto.base64(signature.sign());

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Content another) {
        TESTransaction txn = (TESTransaction) another;
        return type.equals(txn.getType()) &&
                publicKeyHash.equals(txn.getPublicKeyHash()) &&
                signature.equals(txn.getSignature()) &&
                concreteAttributesEquals(another);
    }

    @Override
    public String toString(int tabs) {
        return  toStringWithTabs("TESTransaction: {", tabs) +
                toStringWithTabs("type: " + type, tabs + 1) +
                toStringWithTabs("publicKey: " + publicKeyHash, tabs + 1) +
                toStringWithTabs("signature: " + signature, tabs + 1) +
                toStringWithTabs("}", tabs);
    }


    protected abstract void signConcreteAttributes(Signature signature) throws SignatureException;
    protected abstract boolean concreteAttributesEquals(Content another);

}
