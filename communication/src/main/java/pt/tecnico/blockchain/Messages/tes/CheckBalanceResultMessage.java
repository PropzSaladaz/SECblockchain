package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Messages.Message;
import pt.tecnico.blockchain.Messages.blockchain.TransactionResultMessage;
import pt.tecnico.blockchain.Messages.Content;


public class CheckBalanceResultMessage extends Message implements Content {

    private Integer _amount;

    public CheckBalanceResultMessage(Content message) {
        super(null);
    }

    public CheckBalanceResultMessage(int balance, Content message) {
        super(message);
        _amount = balance;
    }

    public void setAmount(int value) {
        _amount = value;
    }

    public Integer getAmount() {
        return _amount;
    }

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