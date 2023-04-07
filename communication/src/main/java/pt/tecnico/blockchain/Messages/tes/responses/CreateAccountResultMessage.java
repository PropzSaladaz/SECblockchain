package pt.tecnico.blockchain.Messages.tes.responses;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;
import pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction;

import java.security.MessageDigest;

public class CreateAccountResultMessage extends TESResultMessage {

    public CreateAccountResultMessage(int nonce, String sender) {
        super(nonce, sender, TESTransaction.CREATE_ACCOUNT);
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
        return toStringWithTabs("TransactionResultMessage: {", level) +
                super.toString(level + 1) +
                toStringWithTabs("}", level);
    }
}
