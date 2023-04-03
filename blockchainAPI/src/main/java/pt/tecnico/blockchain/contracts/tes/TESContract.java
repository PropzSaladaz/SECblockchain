package pt.tecnico.blockchain.contracts.tes;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.CheckBalance;
import pt.tecnico.blockchain.Messages.tes.ClientAccount;
import pt.tecnico.blockchain.Messages.tes.TESTransaction;
import pt.tecnico.blockchain.Messages.tes.Transfer;
import pt.tecnico.blockchain.KeyGenerate;
import pt.tecnico.blockchain.KeyConverter;
import pt.tecnico.blockchain.contracts.SmartContract;


import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class TESContract implements SmartContract {

    public Map<String, ClientAccount> _clientAccounts;
    public String _contractID;

    public TESContract() throws NoSuchAlgorithmException {
        _clientAccounts = new HashMap<>();
        _contractID = "HARDCODEDCONTRACID";
    }

    @Override
    public String getContractID(){
        return _contractID;
    }

    public boolean hasClient(String publicKey){
        return _clientAccounts.get(publicKey) != null;
    }

    public boolean validateSignature(TESTransaction transaction) {
        return transaction.checkSign();
    }

    public boolean validateTransfer(Transfer transfer){
        return transfer.getAmount() > 0 && hasClient(transfer.getDestinationAddress());
    }
    
    public int getBalance(String sender){
        return _clientAccounts.get(sender).checkBalance();
    }

    public void createMinerAccount(String minerKey){
        _clientAccounts.put(minerKey, new ClientAccount());
    }

    @Override
    public boolean assertTransaction(Content tx) {
        TESTransaction transaction = (TESTransaction) tx;
        switch (transaction.getType()) {
            case TESTransaction.CREATE_ACCOUNT:
                if (validateSignature(transaction) && !hasClient(transaction.getSender())){
                    _clientAccounts.put(transaction.getSender(), new ClientAccount());
                    //if(isMiner) _clientAccounts.get(memberKey).deposit(1000);
                    return true;
                }else return false;
            case TESTransaction.TRANSFER:
                Transfer transfer = (Transfer) transaction;
                if (validateSignature(transaction) && hasClient(transaction.getSender()) && validateTransfer(transfer)){
                    _clientAccounts.get(transaction.getSender()).withdrawal(transfer.getAmount());
                    _clientAccounts.get(transfer.getDestinationAddress()).deposit(transfer.getAmount());
                    //if(isMiner) _clientAccounts.get(memberKey).deposit(5000);
                    return true;
                }else return false;
            case TESTransaction.CHECK_BALANCE:
                if (validateSignature(transaction) && hasClient(transaction.getSender())){
                    //if(isMiner) _clientAccounts.get(memberKey).deposit(3000);
                    CheckBalance checkBalance = (CheckBalance) transaction;
                    checkBalance.setAmount(getBalance(transaction.getSender()));
                    return true;
                }
                return false;
            default:
                System.out.println("ERROR: Could not handle request");
                return false;
        }
    }
}
