package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;


public class TransactionResultMessage extends ApplicationMessage implements Content {

    private BlockchainTransactionStatus _status;
    private String message;
    private Integer _nonce;

    public TransactionResultMessage(int nonce, Content message) {
        super(message);
        _nonce = nonce;
    }

    @Override
    public String getApplicationMessageType() {return TRANSACTION_RESULT_MESSAGE;}

    public String getMessage() {
        return message;
    }

    public void setStatus(BlockchainTransactionStatus status) {
        _status = status;
    }

    public void setStatus(BlockchainTransactionStatus status, String message) {
        _status = status;
        this.message = message;
    }

    public BlockchainTransactionStatus getStatus() {
        return _status;
    }

    public Integer getNonce() {return _nonce;}
    public void setNonce(int value) {_nonce = value;}

    @Override
    public String toString(int level) {
        return toStringWithTabs("TransactionResultMessage: {", level) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
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

}