package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Messages.Content;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

public abstract class TESTransaction implements Content {
    public static final String CREATE_ACCOUNT = "C";
    public static final String TRANSFER = "T";
    public static final String CHECK_BALANCE = "B";

    private String from;
    private String type;
    private byte[] signature;
    private int nonce;

    public TESTransaction(int nonce, String type, String sender) {
        this.type = type;
        this.from = sender;
        this.nonce = nonce;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return from;
    }

    public void setPublicKeyHash(String sender) {
        this.from = sender;
    }

    public byte[] getSignature() {
        return signature;
    }

    public Integer getNonce() {
        return nonce;
    }

    public Pair<String,Integer> getTransactionID() {
        return new Pair<>(from, nonce);
    }

    public void sign(PrivateKey key)  {
        try {
            Signature signature = Crypto.getPrivateSignatureInstance(key);
            signature.update(type.getBytes());
            signature.update(from.getBytes());
            signature.update(Integer.toString(nonce).getBytes());
            signConcreteAttributes(signature);
            this.signature = signature.sign();

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
    }

    public boolean checkSign()  {
        try {
            Signature signaturePublic = Crypto.getPublicSignatureInstance(Crypto.getPublicKeyFromHash(from));
            signaturePublic.update(type.getBytes());
            signaturePublic.update(from.getBytes());
            signaturePublic.update(Integer.toString(nonce).getBytes());
            signConcreteAttributes(signaturePublic);
            return signaturePublic.verify(signature);

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(Content another) {
        TESTransaction txn = (TESTransaction) another;
        return type.equals(txn.getType()) &&
                from.equals(txn.getSender()) &&
                signature.equals(txn.getSignature()) &&
                concreteAttributesEquals(another);
    }

    @Override
    public String toString(int tabs) {
        return  toStringWithTabs("TESTransaction: {", tabs) +
                toStringWithTabs("type: " + type, tabs + 1) +
                toStringWithTabs("from: " + from, tabs + 1) +
                toStringWithTabs("signature: " + signature, tabs + 1) +
                toStringWithTabs("}", tabs);
    }


    protected abstract void signConcreteAttributes(Signature signature) throws SignatureException;
    protected abstract boolean concreteAttributesEquals(Content another);

}
