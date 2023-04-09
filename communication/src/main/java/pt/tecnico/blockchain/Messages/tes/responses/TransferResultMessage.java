package pt.tecnico.blockchain.Messages.tes.responses;

import java.security.MessageDigest;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction;
import pt.tecnico.blockchain.Messages.tes.transactions.TransferTransaction;

public class TransferResultMessage extends TESResultMessage {
    int amount;
    String destination;

    public TransferResultMessage(TransferTransaction txn) {
        super(txn.getNonce(), txn.getSender(), TESTransaction.TRANSFER);
        amount = txn.getAmount();
        destination = txn.getDestinationAddress();
        setFailureReason(txn.getFailureMessage());
    }

    public int getAmount() {
        return amount;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public byte[] digestMessageFields() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(super.digestMessageFields());
            digest.update(Integer.toString(amount).getBytes());
            digest.update(destination.getBytes());
            return digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sign(Integer signerPID) {
        try {
            super.setSignature(Crypto.getSignature(digestMessageFields(), RSAKeyStoreById.getPrivateKey(signerPID)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean concreteTxnEquals(Content another) {
        return false;
    }

    @Override
    protected void updateConcreteHash(MessageDigest d) {
        d.update(Integer.toString(amount).getBytes());
        d.update(destination.getBytes());
    }

    @Override
    public String toString(int level) {
        return toStringWithTabs("TransferResultMessage: {", level) +
                super.toString(level + 1) +
                toStringWithTabs("amount: " + amount, level + 1) +
                toStringWithTabs("destination: " + destination.substring(0, 15), level + 1) +
                toStringWithTabs("}", level);
    }
}
