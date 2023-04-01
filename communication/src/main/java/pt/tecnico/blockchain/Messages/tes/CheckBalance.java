package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Messages.Content;

import java.security.Signature;
import java.security.SignatureException;

public class CheckBalance extends TESTransaction {
    private final String readType;

    public CheckBalance(int nonce, String publicKeyHash, String readType) {
        super(nonce, TESTransaction.CHECK_BALANCE, publicKeyHash);
        this.readType = readType;
    }

    @Override
    protected void signConcreteAttributes(Signature signature) throws SignatureException {
        signature.update(Byte.parseByte(readType));
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
