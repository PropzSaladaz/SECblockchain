package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Messages.Content;

import java.security.Signature;
import java.security.SignatureException;

public class Transfer extends TESTransaction {

    private String destinationAddress;
    private int amount;

    public Transfer(String sourceAddress, String destinationAddress, int amount, int gasPrice, int gasLimit) {
        super(TESTransaction.TRANSFER, sourceAddress, gasPrice, gasLimit);
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
            Transfer txn = (Transfer) another;
            return destinationAddress.equals(txn.getDestinationAddress()) &&
                    amount == txn.getAmount();
        } catch(ClassCastException e) {
            return false;
        }
    }
}
