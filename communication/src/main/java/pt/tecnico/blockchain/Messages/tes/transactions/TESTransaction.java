package pt.tecnico.blockchain.Messages.tes.transactions;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Messages.Content;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public abstract class TESTransaction implements Content {
    public static final String CREATE_ACCOUNT = "C";
    public static final String TRANSFER = "T";
    public static final String CHECK_BALANCE = "B";

    private String from;
    private String type;
    private String senderBalanceHash = "";
    private byte[] signature;
    private final int nonce;

    private String failureMessage = "None"; // should not be persistent in blockchain

    public TESTransaction(int nonce, String type, String sender) {
        this.type = type;
        this.from = sender;
        this.nonce = nonce;
    }

    public String getBalanceHash() {
        return senderBalanceHash;
    }

    public void setSenderBalanceHash(int balance) {
        this.senderBalanceHash = getBase64HashFromBalance(balance);
    }

    protected String getBase64HashFromBalance(int balance) {
        try {
            byte[] bytes = Integer.toString(balance).getBytes();
            return Crypto.base64(Crypto.digest(bytes));
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
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

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
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

    public boolean checkSignature()  {
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
                Arrays.equals(signature, txn.getSignature()) &&
                concreteAttributesEquals(another);
    }

    @Override
    public String toString(int tabs) {
        String senderBalHash = senderBalanceHash.equals("") ? "" : senderBalanceHash.substring(0, 15) + "...";
        return  toStringWithTabs("type: " + type, tabs) +
                toStringWithTabs("nonce: " + nonce, tabs) +
                toStringWithTabs("from: " +  from.substring(0, 15) + "...", tabs) +
                toStringWithTabs("senderBalanceHash: " + senderBalHash, tabs) +
                toStringWithTabs("signature: " + Crypto.base64(signature, 15), tabs) +
                toStringWithTabs("failureMessage: " + failureMessage, tabs);
    }


    protected abstract void signConcreteAttributes(Signature signature) throws SignatureException;
    protected abstract boolean concreteAttributesEquals(Content another);

}
