package pt.tecnico.blockchain.Messages.tes.responses;

import java.security.MessageDigest;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction;

public class TransferResultMessage extends TESResultMessage {
    int amount;
    String destination;

    public TransferResultMessage(int nonce, String sender, int amount, String destination) {
        super(nonce, sender, TESTransaction.TRANSFER);
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
    public String toString(int level) {
        return toStringWithTabs("TransactionResultMessage: {", level) +
                super.toString(level + 1) +
                toStringWithTabs("}", level);
    }
}
