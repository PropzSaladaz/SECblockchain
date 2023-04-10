package pt.tecnico.blockchain.Messages.tes.transactions;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.Content;

import java.nio.ByteBuffer;
import java.security.Signature;
import java.security.SignatureException;

public class TransferTransaction extends TESTransaction {

    private String destinationAddress = "";
    String destinationBalanceHash = "";
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
    protected void signConcreteAttributes(Signature signature) throws SignatureException {
        signature.update(destinationAddress.getBytes());
        signature.update(ByteBuffer.allocate(Integer.BYTES).putInt(amount).array());
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

    @Override
    public String toString(int tabs) {
        String destAddr = destinationAddress.equals("") ? "" : destinationAddress.substring(0, 15);
        String destBalanceHash = destinationBalanceHash.equals("") ? "" : destinationBalanceHash.substring(0, 15);
        return  toStringWithTabs("TransferTransaction: {", tabs) +
                super.toString(tabs + 1) +
                toStringWithTabs("destinationAddress: " + destAddr, tabs + 1) +
                toStringWithTabs("destinationBalanceHash: " + destBalanceHash, tabs + 1) +
                toStringWithTabs("amount: " + amount, tabs + 1) +
                toStringWithTabs("}", tabs);
    }
}
