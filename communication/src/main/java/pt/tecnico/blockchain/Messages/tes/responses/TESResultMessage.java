package pt.tecnico.blockchain.Messages.tes.responses;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.Message;
import pt.tecnico.blockchain.Messages.MessageManager;

public abstract class TESResultMessage extends Message implements Content {
    private final String type;
    private final int txnNonce;
    private final String transactionInvoker;
    private String resultSender;
    private String failureReason = "None";
    private byte[] _signature;

    TESResultMessage(int nonce, String transactionInvoker, String type) {
        txnNonce = nonce;
        this.type = type;
        this.transactionInvoker = transactionInvoker;
    }

    public void setSignature(byte[] signature) {
        _signature = signature;
    }

    public byte[] getSignature() {
        return _signature;
    }

    public String getType() {
        return type;
    }

    public int getTxnNonce() {
        return txnNonce;
    }

    public String getResultSender() {
        return resultSender;
    }

    public void setResultSender(String sender) {
        resultSender = sender;
    }

    public String getTransactionInvoker() {
        return transactionInvoker;
    }

    public String getErrorMessage() {
        return failureReason;
    }

    public void setFailureReason(String message) {
        failureReason = message;
    }

    @Override
    public byte[] digestMessageFields() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            if (getContent() != null) digest.update(MessageManager.getContentBytes(getContent())); // since content is optional
            digest.update(type.getBytes());
            digest.update(Integer.toString(txnNonce).getBytes());
            digest.update(transactionInvoker.getBytes());
            digest.update(resultSender.getBytes());
            digest.update(failureReason.getBytes());
            return digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sign(Integer signerPID) {
        try {
            _signature = Crypto.getSignature(digestMessageFields(), RSAKeyStoreById.getPrivateKey(signerPID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Content another) {
        try {
            TESResultMessage m = (TESResultMessage) another;
            return txnNonce == m.getTxnNonce() &&
                    concreteTxnEquals(m);
        } catch( ClassCastException e) {
            return false;
        }
    }
    protected abstract boolean concreteTxnEquals(Content another);


    @Override
    public String toHash() {
        try { // purposely do not account for resultSender
            MessageDigest d = Crypto.getDigest();
            d.update(type.getBytes());
            d.update(Integer.toString(txnNonce).getBytes());
            d.update(transactionInvoker.getBytes());
            d.update(failureReason.getBytes());
            updateConcreteHash(d);
            return Crypto.base64(d.digest());
        } catch (NoSuchAlgorithmException e) {
            Logger.logError("Could not get hash for TESResultMessage", e);
            throw new RuntimeException();
        }
    }

    protected abstract void updateConcreteHash(MessageDigest d);

    @Override
    public String toString(int level) {
        return toStringWithTabs("type:" + type, level) +
                toStringWithTabs("txnNonce:" + txnNonce, level) +
                toStringWithTabs("txnInvoker: " + transactionInvoker, level) +
                toStringWithTabs("resultSender: " + resultSender, level) +
                toStringWithTabs("signature: " + Crypto.base64(_signature, 15) , level) +
                toStringWithTabs("failureReason: " + failureReason, level);
    }
}
