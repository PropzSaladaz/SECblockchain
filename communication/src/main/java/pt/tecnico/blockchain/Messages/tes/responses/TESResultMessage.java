package pt.tecnico.blockchain.Messages.tes.responses;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.Message;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;

public abstract class TESResultMessage extends Message implements Content {
    private String type;
    private final int txnNonce;
    private String sender;
    private String failureReason = "";

    TESResultMessage(int nonce, String sender, String type) {
        txnNonce = nonce;
        this.type = type;
        this.sender = sender;
    }

    public String getType() {
        return type;
    }
    public int getTxnNonce() {
        return txnNonce;
    }

    public String getSender() {
        return sender;
    }

    public String getErrorMessage() {
        return failureReason;
    }

    public void setFailureReason(String message) {
        failureReason = message;
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
    public String toString(int level) {
        return toStringWithTabs("nonce:" + txnNonce, level) +
                toStringWithTabs("failureReason:" + failureReason, level);
    }
}
