package pt.tecnico.blockchain.contracts.tes;

import pt.tecnico.blockchain.KeyConverter;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.*;
import pt.tecnico.blockchain.Messages.tes.responses.CheckBalanceResultMessage;
import pt.tecnico.blockchain.Messages.tes.responses.CreateAccountResultMessage;
import pt.tecnico.blockchain.Messages.tes.responses.TransferResultMessage;
import pt.tecnico.blockchain.Messages.tes.transactions.CheckBalanceTransaction;
import pt.tecnico.blockchain.Messages.tes.transactions.CreateAccountTransaction;
import pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction;
import pt.tecnico.blockchain.Messages.tes.transactions.TransferTransaction;
import pt.tecnico.blockchain.contracts.SmartContract;

import java.security.interfaces.RSAKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction.*;

public class TESContract implements SmartContract {

    public Map<String, ClientAccount> _clientAccounts;
    public static String _contractID = "TESCONTRACTID";
    public List<String> _minerList;

    public TESContract() {
        _clientAccounts = new HashMap<>();
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
        for(String miner : _minerList){ createMinerAccount(miner); }
    }

    public boolean validateSignature(TESTransaction transaction) {
        return transaction.checkSignature();
    }

    public void createMinerAccount(String minerKey){
        _clientAccounts.put(minerKey, new ClientAccount());
    }

    public int getAccountCurrentBalance(String accountId){
        if (_clientAccounts.containsKey(accountId)) {
            return _clientAccounts.get(accountId).getCurrentBalance();
        }
        return -1;
    }

    public int getAccountPreviousBalance(String accountId){
        if (_clientAccounts.containsKey(accountId)) {
            return _clientAccounts.get(accountId).getPreviousBalance();
        }
        return -1;
    }

    public Content getAccountBalanceProof(String account_id){
        return _clientAccounts.get(account_id).getBalanceProof();
    }

    @Override
    public Content getTransactionResponse(Content transaction, String memberPubKey) {
        try {
            TESTransaction txn = (TESTransaction) transaction;
            switch (txn.getType()) {
                case CHECK_BALANCE:
                    return getCheckBalanceResponse((CheckBalanceTransaction) transaction, memberPubKey);
                case TRANSFER:
                    return getTransferResponse((TransferTransaction) transaction, memberPubKey);
                case CREATE_ACCOUNT:
                    return getCreateAccountResponse((CreateAccountTransaction) transaction, memberPubKey);
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * used to set the current hashed balances of all affected accounts in all txns. This hashed value
     * will then be used when client issues weak reads so that the client can compare the previousBalance received
     * with the one on a txn on the block.
     */
    @Override
    public void updateTransactionWithCurrentState(Content transaction) {
        TESTransaction txn = (TESTransaction) transaction;
        String sender = txn.getSender();
        switch (txn.getType()) {
            case CHECK_BALANCE:
                CheckBalanceTransaction checkT = (CheckBalanceTransaction) txn;
                checkT.setSenderBalanceHash(getAccountCurrentBalance(sender));
                break;
            case TRANSFER:
                TransferTransaction transferT = (TransferTransaction) txn;
                transferT.setSenderBalanceHash(getAccountCurrentBalance(sender));
                transferT.setDestinationBalanceHash(getAccountCurrentBalance(transferT.getDestinationAddress()));
                break;
            case CREATE_ACCOUNT:
                CreateAccountTransaction createT = (CreateAccountTransaction) txn;
                createT.setSenderBalanceHash(getAccountCurrentBalance(sender));
                break;
            default:
                break;
        }
    }

    private Content getCheckBalanceResponse(CheckBalanceTransaction transaction, String memberPubKey) throws Exception {
        CheckBalanceResultMessage response = new CheckBalanceResultMessage(
                transaction.getNonce(),
                transaction.getSender()
        );
        String from = transaction.getSender();
        switch (transaction.getReadType()) {
            case STRONG:
                response.setAmount(_clientAccounts.get(from).getCurrentBalance());
                response.setReadType(TESReadType.STRONG);
                response.setFailureReason(transaction.getFailureMessage());
                response.sign(RSAKeyStoreById.getPidFromPublic(KeyConverter.base64ToPublicKey(memberPubKey)));
                return response;
            case WEAK:
                response.setAmount(_clientAccounts.get(from).getPreviousBalance());
                response.setReadType(TESReadType.WEAK);
                response.setContent(getAccountBalanceProof(from));
                response.setFailureReason(transaction.getFailureMessage());
                response.sign(RSAKeyStoreById.getPidFromPublic(KeyConverter.base64ToPublicKey(memberPubKey)));
                return response;
            default:
                Logger.logError("Unknown readType for CheckBalanceTransaction");
                return null;
        }
    }

    private Content getTransferResponse(TransferTransaction transaction, String memberPubKey) throws Exception {
        TransferResultMessage response = new TransferResultMessage(
                transaction.getNonce(),
                transaction.getSender(),
                transaction.getAmount(),
                transaction.getDestinationAddress()
        );
        response.setFailureReason(transaction.getFailureMessage());
        response.sign(RSAKeyStoreById.getPidFromPublic(KeyConverter.base64ToPublicKey(memberPubKey)));
        return response;
    }

    private Content getCreateAccountResponse(CreateAccountTransaction transaction, String memberPubKey) throws Exception {
        CreateAccountResultMessage response = new CreateAccountResultMessage(
                transaction.getNonce(),
                transaction.getSender()
        );
        response.setFailureReason(transaction.getFailureMessage());
        response.sign(RSAKeyStoreById.getPidFromPublic(KeyConverter.base64ToPublicKey(memberPubKey)));
        return response;
    }


    @Override
    public boolean validateAndExecuteTransaction(Content tx, String minerKey, Content transactionsProof) {
        TESTransaction transaction = (TESTransaction) tx;
        if (validateSignature(transaction)) {
            switch (transaction.getType()) {
                case TESTransaction.CHECK_BALANCE:
                    return validateAndExecuteCheckBalance((CheckBalanceTransaction) transaction, minerKey);
                case CREATE_ACCOUNT:
                    return validateAndExecuteCreateAccount((CreateAccountTransaction) transaction, minerKey, transactionsProof);
                case TRANSFER:
                    return validateAndExecuteTransfer((TransferTransaction) transaction, minerKey, transactionsProof);
                default:
                    Logger.logError("ERROR: Could not handle request");
                    return false;
            }
        } else return false;
    }

    private void executeCreateAccount(CreateAccountTransaction transaction, String minerKey, Content transactionsProof) {
        _clientAccounts.put(transaction.getSender(), new ClientAccount());
        _clientAccounts.get(transaction.getSender()).updateBalanceProof(transactionsProof);
        payMiner(minerKey, 1000);
    }

    private boolean validateAndExecuteCreateAccount(CreateAccountTransaction transaction, String minerKey, Content transactionsProof) {
        if (!clientAccountExists(transaction.getSender())){
            executeCreateAccount(transaction, minerKey, transactionsProof);
            return true;
        } else {
            transaction.setFailureMessage("Account with ID " + transaction.getSender() + " already exists.");
            return false;
        }
    }

    private void executeTransfer(TransferTransaction transaction, String minerKey, Content transactionsProof) {
        _clientAccounts.get(transaction.getSender()).withdrawal(transaction.getAmount());
        _clientAccounts.get(transaction.getDestinationAddress()).deposit(transaction.getAmount());
        _clientAccounts.get(transaction.getSender()).updateBalanceProof(transactionsProof);
        _clientAccounts.get(transaction.getDestinationAddress()).updateBalanceProof(transactionsProof);
        payMiner(minerKey, 5000);
    }

    private boolean validateAndExecuteTransfer(TransferTransaction transaction, String minerKey, Content transactionsProof) {
        if (validateTransfer(transaction)) {
            executeTransfer(transaction, minerKey, transactionsProof);
            return true;
        } else {
            return false;
        }
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
        if(!(amountToTransfer > 0)) {
            transfer.setFailureMessage("Amount to transfer must be > than 0.");
            return false;
        }
        if (!clientAccountExists(transfer.getDestinationAddress())) {
            transfer.setFailureMessage("Sender account doesn't exist");
            return false;
        }
        if (!clientAccountExists(transfer.getSender())) {
            transfer.setFailureMessage("Receiver account doesn't exist");
            return false;
        }
        if (!_clientAccounts.get(transfer.getSender()).hasBalanceGreaterThan(amountToTransfer)) {
            transfer.setFailureMessage("Receiver account doesn't exist");
            return  false;
        }
        return true;
    }
}
