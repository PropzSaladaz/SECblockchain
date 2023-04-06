package pt.tecnico.blockchain.Messages.tes.transactions;

import pt.tecnico.blockchain.Messages.Content;

import java.security.Signature;

public class CreateAccountTransaction extends TESTransaction {


    public CreateAccountTransaction(int nonce, String publicKeyHash) {
        super(nonce, TESTransaction.CREATE_ACCOUNT, publicKeyHash);
    }

    @Override
    protected void signConcreteAttributes(Signature signature) {
        // this class doesn't have any additional attributes
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

    @Override
    public String toString(int tabs) {
        return  toStringWithTabs("CreateAccountTransaction: {", tabs) +
                super.toString(tabs + 1) +
                toStringWithTabs("}", tabs);
    }

}
