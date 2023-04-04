package pt.tecnico.blockchain.contracts.tes;

import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.*;
import pt.tecnico.blockchain.contracts.SmartContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TESContract implements SmartContract {

    public Map<String, ClientAccount> _clientAccounts;
    public String _contractID;
    public List<String> _minerList;

    public TESContract() {
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
        return transaction.checkSignature();
    }

    public void createMinerAccount(String minerKey){
        _clientAccounts.put(minerKey, new ClientAccount());
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


    @Override
    public Content executeReadTransaction(Content tx) {
        TESTransaction transaction = (TESTransaction) tx;
        switch (transaction.getType()) {
            case TESTransaction.CHECK_BALANCE:
                CheckBalanceTransaction txRequest = (CheckBalanceTransaction) transaction;
                CheckBalanceResultMessage txResult = new CheckBalanceResultMessage(null);
                String txSender = txRequest.getSender();

                if (txRequest.getReadType() == TESReadType.WEAK) {
                    txResult.setAmount(getAccountPreviousBalance(txSender));
                    txResult.setContent(getAccountLastBlockWritten(txSender));
                } else if (txRequest.getReadType() == TESReadType.STRONG) {
                    txResult.setContent(txResult);
                }
                return txResult;
            default:
                System.out.println("ERROR: Could not handle request");
                return null;
        }
    }

    @Override
    public boolean assertTransaction(Content tx, String minerKey) {
        TESTransaction transaction = (TESTransaction) tx;
        if (validateSignature(transaction)) {
            switch (transaction.getType()) {
                case TESTransaction.CREATE_ACCOUNT:
                    return validateAndExecuteCreateAccount((CreateAccountTransaction) transaction, minerKey);
                case TESTransaction.TRANSFER:
                    return validateAndExecuteTransfer((TransferTransaction) transaction, minerKey);
                case TESTransaction.CHECK_BALANCE:
                    return validateAndExecuteCheckBalance((CheckBalanceTransaction) transaction, minerKey);
                default:
                    Logger.logError("ERROR: Could not handle request");
                    return false;
            }
        } else return false;
    }

    private boolean validateAndExecuteCreateAccount(CreateAccountTransaction transaction, String minerKey) {
        if (!clientAccountExists(transaction.getSender())){
            _clientAccounts.put(transaction.getSender(), new ClientAccount());
            payMiner(minerKey, 1000);
            return true;
        } else return false;
    }

    private boolean validateAndExecuteTransfer(TransferTransaction transaction, String minerKey) {
        if (validateTransfer(transaction)){
            _clientAccounts.get(transaction.getSender()).withdrawal(transaction.getAmount());
            _clientAccounts.get(transaction.getDestinationAddress()).deposit(transaction.getAmount());
            payMiner(minerKey, 5000);
            return true;
        } else return false;
    }

    private boolean validateAndExecuteCheckBalance(CheckBalanceTransaction transaction, String minerKey) {
        return (clientAccountExists(transaction.getSender()));
    }

    private void payMiner(String minerKey, int amount) { // TODO transfer from the client account! check if client has the necessary balance
        if(_minerList.contains(minerKey)) _clientAccounts.get(minerKey).deposit(amount);
    }

    private boolean clientAccountExists(String publicKey) {
        return _clientAccounts.containsKey(publicKey);
    }

    private boolean validateTransfer(TransferTransaction transfer) {
        int amountToTransfer = transfer.getAmount();
        return  amountToTransfer > 0 &&
                clientAccountExists(transfer.getDestinationAddress()) &&
                clientAccountExists(transfer.getSender()) &&
                _clientAccounts.get(transfer.getSender()).hasBalanceGreaterThan(amountToTransfer);
    }
}
