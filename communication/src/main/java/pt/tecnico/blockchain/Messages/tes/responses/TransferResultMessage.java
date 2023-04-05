package pt.tecnico.blockchain.Messages.tes.responses;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;
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
