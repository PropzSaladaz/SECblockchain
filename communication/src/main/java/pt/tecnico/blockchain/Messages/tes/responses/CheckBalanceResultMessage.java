package pt.tecnico.blockchain.Messages.tes.responses;

import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;

import java.security.MessageDigest;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.TESReadType;
import pt.tecnico.blockchain.Messages.tes.transactions.CheckBalanceTransaction;
import pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction;


public class CheckBalanceResultMessage extends TESResultMessage implements Content {

    private int _amount;
    private TESReadType readType;

    public CheckBalanceResultMessage(CheckBalanceTransaction txn) {
        super(txn.getNonce(), txn.getSender(), TESTransaction.CHECK_BALANCE);
        readType = txn.getReadType();
        setFailureReason(txn.getFailureMessage());
    }

    public void setAmount(int _amount) {
        this._amount = _amount;
    }

    public void setReadType(TESReadType readType) {
        this.readType = readType;
    }

    public int getAmount() {
        return _amount;
    }

    public TESReadType getReadType() {
        return readType;
    }

    @Override
    public byte[] digestMessageFields() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(super.digestMessageFields());
            digest.update(Integer.toString(_amount).getBytes());
            digest.update(readType.getCode().getBytes());
            return digest.digest();
        } catch (Exception e) {
            Logger.logError("Could not digestMessageFields", e);
            return null;
        }
    }

    @Override
    public void sign(Integer signerPID) {
        try {
            super.setSignature(Crypto.getSignature(digestMessageFields(), RSAKeyStoreById.getPrivateKey(signerPID)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean concreteTxnEquals(Content another) {
        try {
            CheckBalanceResultMessage m = (CheckBalanceResultMessage) another;
            return _amount == m.getAmount() && readType == m.getReadType();
        } catch( ClassCastException e) {
            return false;
        }
    }

    @Override
    protected void updateConcreteHash(MessageDigest d) {
        d.update(Integer.toString(_amount).getBytes());
        d.update(readType.getCode().getBytes());
    }

    @Override
    public String toString(int level) {
        return toStringWithTabs("CheckBalanceResultMessage: {", level) +
                super.toString(level + 1) +
                toStringWithTabs("amount:" + _amount, level+1) +
                toStringWithTabs("readType:" + readType, level+1) +
                toStringWithTabs("}", level);
    }
}