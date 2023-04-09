package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;


public class TransactionResultMessage extends ApplicationMessage implements Content {
    private static final String EMPY_FAILURE_MESSAGE = "None";

    private BlockchainTransactionStatus _status;
    private BlockchainTransactionType transactionType;
    private Integer _nonce;

    private String failureMessage = EMPY_FAILURE_MESSAGE;

    public TransactionResultMessage(int nonce, Content message,  BlockchainTransactionType type, BlockchainTransactionStatus status) {
        super(message);
        _nonce = nonce;
        _status = status;
        transactionType = type;
    }

    public TransactionResultMessage(int nonce,  BlockchainTransactionType type) {
        super(null);
        _nonce = nonce;
        _status = BlockchainTransactionStatus.REJECTED;
        transactionType = type;
    }

    @Override
    public String getApplicationMessageType() {return TRANSACTION_RESULT_MESSAGE;}

    public void setStatus(BlockchainTransactionStatus status) {
        _status = status;
    }

    public void setStatus(BlockchainTransactionStatus status, String failureMessage) {
        _status = status;
        this.failureMessage = failureMessage;
    }

    public BlockchainTransactionType getTransactionType() {
        return transactionType;
    }

    public BlockchainTransactionStatus getStatus() {
        return _status;
    }

    public Integer getNonce() {return _nonce;}

    public void setNonce(int value) {_nonce = value;}

    public boolean encounteredError() {
        return !failureMessage.equals(EMPY_FAILURE_MESSAGE);
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    @Override
    public boolean equals(Content another) {
        try {
            TransactionResultMessage m = (TransactionResultMessage) another;
            return this.getContent() == m.getContent();
        } catch( ClassCastException e) {
            return false;
        }

    }

    @Override
    public String toString(int level) {
        String contentMessage = (getContent() != null) ? getContent().toString(level + 1) : "No content";
        return toStringWithTabs("TransactionResultMessage: {", level) +
                toStringWithTabs("status: " + _status.getMessage(), level + 1) +
                toStringWithTabs("nonce: " + _nonce, level + 1) +
                contentMessage +
                toStringWithTabs("}", level);
    }

}