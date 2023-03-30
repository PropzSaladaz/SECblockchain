package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Messages.Content;

import java.security.Signature;

public class CreateAccount extends TESTransaction {


    public CreateAccount(String publicKeyHash, int gasPrice, int gasLimit) {
        super(TESTransaction.CREATE_ACCOUNT, publicKeyHash, gasPrice, gasLimit);
    }

    @Override
    protected void signConcreteAttributes(Signature signature) {
        // this class doesn't have any additional attributes
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
