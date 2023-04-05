package pt.tecnico.blockchain.Messages.tes.responses;

import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.TESReadType;
import pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction;


public class CheckBalanceResultMessage extends TESResultMessage implements Content {

    private int _amount;
    private TESReadType readType;

    public CheckBalanceResultMessage(int nonce, String sender) {
        super(nonce, sender, TESTransaction.CHECK_BALANCE);
    }

    public CheckBalanceResultMessage(int nonce, String sender, int balance, TESReadType readType) {
        super(nonce, sender, TESTransaction.CHECK_BALANCE);
        _amount = balance;
        this.readType = readType;
    }

    public void setAmount(int _amount) {
        this._amount = _amount;
    }

    public void setReadType(TESReadType readType) {
        this.readType = readType;
    }

    public int getAmount() {
        return _amount;
    }

    public TESReadType getReadType() {
        return readType;
    }

    @Override
    public boolean concreteTxnEquals(Content another) {
        try {
            CheckBalanceResultMessage m = (CheckBalanceResultMessage) another;
            return _amount == m.getAmount() && readType == m.getReadType();
        } catch( ClassCastException e) {
            return false;
        }
    }

    @Override
    public String toString(int level) {
        return toStringWithTabs("CheckBalanceResultMessage: {", level) +
                super.toString(level + 1) +
                toStringWithTabs("amount:" + _amount, level+1) +
                toStringWithTabs("readType:" + readType, level+1) +
                toStringWithTabs("}", level);
    }
}