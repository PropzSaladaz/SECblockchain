package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Messages.Content;

import java.security.Signature;
import java.security.SignatureException;

public class CheckBalanceTransaction extends TESTransaction {
    public static final String WEAK_READ = "WEAK_READ";
    public static final String STRONG_READ = "STRONG_READ";

    public String _readType;

    public CheckBalanceTransaction(int nonce, String publicKeyHash, String readType) {
        super(nonce, TESTransaction.CHECK_BALANCE, publicKeyHash);
        _readType = readType;
    }

    public String getReadType() {
        return _readType;
    }

    @Override
    protected void signConcreteAttributes(Signature signature) throws SignatureException {
    }

    @Override
    protected boolean concreteAttributesEquals(Content another) {
        try {
            CheckBalanceTransaction txn = (CheckBalanceTransaction) another;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
