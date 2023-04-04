package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Messages.Content;

import java.security.Signature;
import java.security.SignatureException;

public class CheckBalance extends TESTransaction {
    private int _amount;
    private final TESReadType readType;

    public CheckBalance(int nonce, String publicKeyHash, TESReadType readType) {
        super(nonce, TESTransaction.CHECK_BALANCE, publicKeyHash);
        this.readType = readType;
    }

    public int getAmount(){return _amount;}

    public TESReadType getReadType() {
        return readType;
    }

    public void setAmount(int amount) {_amount = amount;}

    @Override
    protected void signConcreteAttributes(Signature signature) throws SignatureException {
        signature.update((byte) readType.getCode());
    }

    @Override
    protected boolean concreteAttributesEquals(Content another) {
        try {
            CheckBalance txn = (CheckBalance) another;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
