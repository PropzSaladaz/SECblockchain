package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Messages.Content;

import java.security.Signature;
import java.security.SignatureException;

public class CheckBalance extends TESTransaction {

    public CheckBalance(int nonce, String publicKeyHash) {
        super(nonce, TESTransaction.CHECK_BALANCE, publicKeyHash);
    }

    @Override
    protected void signConcreteAttributes(Signature signature) throws SignatureException {
        // no additional attributes to sign
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
