package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Messages.Content;

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
        signature.update((byte) readType.getCode());
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
