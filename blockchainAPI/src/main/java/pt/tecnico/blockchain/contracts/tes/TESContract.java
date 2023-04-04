package pt.tecnico.blockchain.contracts.tes;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.tes.CheckBalanceResultMessage;
import pt.tecnico.blockchain.Messages.tes.CheckBalanceTransaction;
import pt.tecnico.blockchain.Messages.tes.ClientAccount;
import pt.tecnico.blockchain.Messages.tes.TESTransaction;
import pt.tecnico.blockchain.Messages.tes.TransferTransaction;
import pt.tecnico.blockchain.KeyGenerate;
import pt.tecnico.blockchain.KeyConverter;
import pt.tecnico.blockchain.contracts.SmartContract;


import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TESContract implements SmartContract {

    public Map<String, ClientAccount> _clientAccounts;
    public String _contractID;
    public List<String> _minerList;

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

    public void setMiners(List<String> minerList){
        _minerList = minerList;
        for(String miner : _minerList){ createMinerAccount(miner);}
    }

    public boolean validateSignature(TESTransaction transaction) {
        return transaction.checkSign();
    }

    public boolean validateTransfer(TransferTransaction transfer){
        return transfer.getAmount() > 0 && hasClient(transfer.getDestinationAddress());
    }
    
    public int getAccountCurrentBalance(String account_id){
        return _clientAccounts.get(account_id).getCurrentBalance();
    }

    public int getAccountPreviousBalance(String account_id){
        return _clientAccounts.get(account_id).getPreviousBalance();
    }

    public Content getAccountLastBlockWritten(String account_id){
        return _clientAccounts.get(account_id).getLastBlockToWriteOnBalance();
    }

    public void createMinerAccount(String minerKey){
        _clientAccounts.put(minerKey, new ClientAccount());
    }

    @Override
    public Content executeReadTransaction(Content tx) {
        TESTransaction transaction = (TESTransaction) tx;
        switch (transaction.getType()) {
            case TESTransaction.CHECK_BALANCE:
                CheckBalanceTransaction txRequest = (CheckBalanceTransaction) transaction;
                CheckBalanceResultMessage txResult = new CheckBalanceResultMessage(null);
                String txSender = txRequest.getSender();

                if (txRequest.getType() == BlockchainTransaction.WEAK_READ) {
                    txResult.setAmount(getAccountPreviousBalance(txSender));
                    txResult.setContent(getAccountLastBlockWritten(txSender));
                } else if (txRequest.getType() == BlockchainTransaction.STRONG_READ) {
                    txResult.setContent(txResult);
                }
                return txResult;
            default:
                System.out.println("ERROR: Could not handle request");
                return null;
        }
    }

    @Override
    public boolean assertTransaction(Content tx,String minerKey) {
        TESTransaction transaction = (TESTransaction) tx;
        switch (transaction.getType()) {
            case TESTransaction.CHECK_BALANCE:
                return validateSignature(transaction) && hasClient(transaction.getSender());

            case TESTransaction.CREATE_ACCOUNT:
                if (validateSignature(transaction) && !hasClient(transaction.getSender())){
                    _clientAccounts.put(transaction.getSender(), new ClientAccount());
                    if(_minerList.contains(minerKey)) _clientAccounts.get(minerKey).deposit(1000);
                    return true;
                } else return false;
                
            case TESTransaction.TRANSFER:
                TransferTransaction transfer = (TransferTransaction) transaction;
                if (validateSignature(transaction) && hasClient(transaction.getSender()) && validateTransfer(transfer)){
                    _clientAccounts.get(transaction.getSender()).withdrawal(transfer.getAmount());
                    _clientAccounts.get(transfer.getDestinationAddress()).deposit(transfer.getAmount());
                    if(_minerList.contains(minerKey)) _clientAccounts.get(minerKey).deposit(5000);
                    return true;
                } else return false;
                
            default:
                System.out.println("ERROR: Could not handle request");
                return false;
        }
    }
}
