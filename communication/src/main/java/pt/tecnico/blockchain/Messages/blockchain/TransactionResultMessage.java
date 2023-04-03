package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;


public class TransactionResultMessage extends ApplicationMessage implements Content {
    public static final String SUCCESSFUL_TRANSACTION = "SUCCESSFUL TRANSACTION";
    public static final String REJECTED_TRANSACTION = "REJECTED TRANSACTION";

    private String _status;

    public TransactionResultMessage(Content message) {
        super(message);
    }

    @Override
    public String getApplicationMessageType() {return TRANSACTION_RESULT_MESSAGE;}

    public void setStatus(String status) {_status = status;}

    public String getStatus() {return _status;}

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