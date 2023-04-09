package pt.tecnico.blockchain.Messages.tes.transactions;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.TESReadType;

import java.security.Signature;
import java.security.SignatureException;

public class CheckBalanceTransaction extends TESTransaction {
    private final TESReadType readType;

    public CheckBalanceTransaction(int nonce, String publicKeyHash, TESReadType readType) {
        super(nonce, TESTransaction.CHECK_BALANCE, publicKeyHash);
        this.readType = readType;
    }

    public TESReadType getReadType() {
        return readType;
    }

    @Override
    protected void signConcreteAttributes(Signature signature) throws SignatureException {
        signature.update(readType.getCode().getBytes());
    }

    @Override
    protected boolean concreteAttributesEquals(Content another) {
        try {
            CheckBalanceTransaction txn = (CheckBalanceTransaction) another;
            return readType.equals(txn.getReadType());
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public String toString(int tabs) {
        return  toStringWithTabs("CheckBalanceTransaction: {", tabs) +
                toStringWithTabs("readType: " + readType.getCode(), tabs + 1) +
                super.toString(tabs + 1) +
                toStringWithTabs("}", tabs);
    }
}
