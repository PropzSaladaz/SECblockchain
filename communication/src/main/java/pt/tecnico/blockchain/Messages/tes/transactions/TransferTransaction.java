package pt.tecnico.blockchain.Messages.tes.transactions;

import pt.tecnico.blockchain.Messages.Content;

import java.security.Signature;
import java.security.SignatureException;

public class TransferTransaction extends TESTransaction {

    private String destinationAddress;
    String destinationBalanceHash;
    private int amount;

    public TransferTransaction(int nonce, String sourceAddress, String destinationAddress, int amount) {
        super(nonce, TESTransaction.TRANSFER, sourceAddress);
        this.destinationAddress = destinationAddress;
        this.amount = amount;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDestinationBalanceHash() {
        return destinationBalanceHash;
    }

    public void setDestinationBalanceHash(int destinationBalance) {
        this.destinationBalanceHash = getBase64HashFromBalance(destinationBalance);
    }

    @Override
    public String toString(int tabs) {
        return null;
    }

    @Override
    protected void signConcreteAttributes(Signature signature) throws SignatureException {
        signature.update(Byte.parseByte(destinationAddress));
        signature.update((byte) amount);
    }

    @Override
    protected boolean concreteAttributesEquals(Content another) {
        try {
            TransferTransaction txn = (TransferTransaction) another;
            return destinationAddress.equals(txn.getDestinationAddress()) &&
                    amount == txn.getAmount();
        } catch(ClassCastException e) {
            return false;
        }
    }
}
