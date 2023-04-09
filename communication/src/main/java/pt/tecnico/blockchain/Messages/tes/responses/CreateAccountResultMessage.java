package pt.tecnico.blockchain.Messages.tes.responses;

import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;
import pt.tecnico.blockchain.Messages.tes.transactions.CreateAccountTransaction;
import pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction;

import java.security.MessageDigest;

public class CreateAccountResultMessage extends TESResultMessage {

    public CreateAccountResultMessage(CreateAccountTransaction txn) {
        super(txn.getNonce(), txn.getSender(), TESTransaction.CREATE_ACCOUNT);
        setFailureReason(txn.getFailureMessage());
    }

    @Override
    protected boolean concreteTxnEquals(Content another) {
        return false;
    }

    @Override
    protected void updateConcreteHash(MessageDigest d) {
        // doesn't have any extra fields
    }

    @Override
    public String toString(int level) {
        return toStringWithTabs("CreateAccountResultMessage: {", level) +
                super.toString(level + 1) +
                toStringWithTabs("}", level);
    }
}
