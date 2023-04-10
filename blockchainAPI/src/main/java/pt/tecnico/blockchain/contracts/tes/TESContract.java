package pt.tecnico.blockchain.contracts.tes;

import pt.tecnico.blockchain.KeyConverter;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;
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

import static pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus.VALIDATED;
import static pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction.*;

public class TESContract implements SmartContract {

    private Map<String, ClientAccount> _clientAccounts;
    private static String _contractID = "TESCONTRACTID";
    private List<String> _minerList;
    private int pid;

    public TESContract(int pid) {
        _clientAccounts = new HashMap<>();
        this.pid = pid;
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

    public int getAccountPreviousBalance(String accountId){
        if (_clientAccounts.containsKey(accountId)) {
            return _clientAccounts.get(accountId).getPreviousBalance();
        }
        return -1;
    }

    @Override
    public Content getTransactionResponse(Content transaction, BlockchainTransactionStatus status,
                                          String memberPubKey) {
        try {
            TESTransaction txn = (TESTransaction) transaction;
            switch (txn.getType()) {
                case CHECK_BALANCE:
                    return getCheckBalanceResponse((CheckBalanceTransaction) transaction, status, memberPubKey);
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

    public int getAccountCurrentBalance(String accountId){
        if (_clientAccounts.containsKey(accountId)) {
            return _clientAccounts.get(accountId).getCurrentBalance();
        }
        return -1;
    }

    private Content getCheckBalanceResponse(CheckBalanceTransaction transaction, BlockchainTransactionStatus status,
                                            String memberPubKey) {
        String from = transaction.getSender();
        CheckBalanceResultMessage response = new CheckBalanceResultMessage(transaction);
        response.setResultSender(memberPubKey);
        switch (transaction.getReadType()) {
            case STRONG:
                response.setAmount(getAccountCurrentBalance(from));
                response.sign(RSAKeyStoreById.getPidFromPublic(memberPubKey));
                return response;
            case WEAK:
                response.setAmount(getAccountPreviousBalance(from));
                response.setContent(getAccountBalanceProof(from));
                response.sign(RSAKeyStoreById.getPidFromPublic(memberPubKey));
                return response;
            default:
                Logger.logError("Unknown readType for CheckBalanceTransaction");
                return null;
        }
    }

    public Content getAccountBalanceProof(String account_id){
        if (_clientAccounts.containsKey(account_id)) {
            return _clientAccounts.get(account_id).getBalanceProof();
        } else return null;
    }

    private Content getTransferResponse(TransferTransaction transaction, String memberPubKey) {
        TransferResultMessage response = new TransferResultMessage(transaction);
        response.setResultSender(memberPubKey);
        response.sign(RSAKeyStoreById.getPidFromPublic(memberPubKey));
        return response;
    }

    private Content getCreateAccountResponse(CreateAccountTransaction transaction, String memberPubKey) {
        CreateAccountResultMessage response = new CreateAccountResultMessage(transaction);
        response.setResultSender(memberPubKey);
        response.sign(RSAKeyStoreById.getPidFromPublic(memberPubKey));
        return response;
    }


    @Override
    public boolean validateAndExecuteTransaction(Content tx, String minerKey, Content transactionsProof) {
        TESTransaction transaction = (TESTransaction) tx;
        if (validateSignature(transaction)) {
            switch (transaction.getType()) {
                case TESTransaction.CHECK_BALANCE:
                    return validateAndExecuteCheckBalance((CheckBalanceTransaction) transaction, _clientAccounts);
                case CREATE_ACCOUNT:
                    return validateAndExecuteCreateAccount((CreateAccountTransaction) transaction, minerKey, transactionsProof, _clientAccounts);
                case TRANSFER:
                    return validateAndExecuteTransfer((TransferTransaction) transaction, minerKey, transactionsProof, _clientAccounts);
                default:
                    Logger.logError("Unknown TES transaction type");
                    return false;
            }
        } else return false;
    }

    @Override
    public boolean validateTransaction(Content tx, Object tempState) {
        try {
            TESTransaction transaction = (TESTransaction) tx;
            if (validateSignature(transaction) && tempState != null) {
                // Apply all txns to the temporary validation map to solve txn dependencies
                Map<String, ClientAccount> validationMap = (Map<String, ClientAccount>) tempState;
                switch (transaction.getType()) {
                    case TESTransaction.CHECK_BALANCE:
                        return validateAndExecuteCheckBalance((CheckBalanceTransaction) transaction, validationMap);
                    case CREATE_ACCOUNT:
                        return validateAndExecuteCreateAccount((CreateAccountTransaction) transaction, null,null, validationMap);
                    case TRANSFER:
                        return validateAndExecuteTransfer((TransferTransaction) transaction, null, null, validationMap);
                    default:
                        Logger.logError("Unknown TES transaction type");
                        return false;
                }
            } else return false;
        } catch (ClassCastException e) {
            Logger.logWarning("Got an unexpected message type when validating Transaction in TESContract", e);
            return false;
        }
    }

    @Override
    public Object getNewValidationState() {
        return new HashMap<String, ClientAccount>();
    }

    private boolean validateAndExecuteCreateAccount(CreateAccountTransaction transaction, String minerKey,
                                                    Content transactionsProof, Map<String, ClientAccount> resState) {
        if (validateCreateAccount(transaction, resState)){
            executeCreateAccount(transaction, minerKey, transactionsProof, resState);
            return true;
        } else {
            transaction.setFailureMessage("Account with ID " + transaction.getSender() + " already exists.");
            return false;
        }
    }

    private boolean validateCreateAccount(CreateAccountTransaction transaction, Map<String, ClientAccount> resState) {
        return !clientAccountExists(transaction.getSender(), resState);
    }

    private void executeCreateAccount(CreateAccountTransaction transaction, String minerKey, Content transactionsProof,
                                      Map<String, ClientAccount> resState) {
        resState.put(transaction.getSender(), new ClientAccount());
        Logger.logInfo("Added client acc to map " +  resState.size());
        resState.get(transaction.getSender()).updateBalanceProof(transactionsProof);
        if (resState.equals(_clientAccounts)) payMiner(minerKey, 100);
    }

    private boolean validateCheckBalance(CheckBalanceTransaction transaction, Map<String, ClientAccount> resState) {
        if (clientAccountExists(transaction.getSender(), resState)) {
            return true;
        }
        transaction.setFailureMessage("Client account doesn't exist");
        return false;
    }

    private boolean validateAndExecuteTransfer(TransferTransaction transaction, String minerKey,
                                               Content transactionsProof,  Map<String, ClientAccount> resState) {
        if (validateTransfer(transaction, resState)) {
            executeTransfer(transaction, minerKey, transactionsProof, resState);
            return true;
        } else {
            return false;
        }
    }

    private void executeTransfer(TransferTransaction transaction, String minerKey, Content transactionsProof,
                                 Map<String, ClientAccount> resState) {
        resState.get(transaction.getSender()).withdrawal(transaction.getAmount());
        resState.get(transaction.getDestinationAddress()).deposit(transaction.getAmount());
        resState.get(transaction.getSender()).updateBalanceProof(transactionsProof);
        resState.get(transaction.getDestinationAddress()).updateBalanceProof(transactionsProof);
        if (resState.equals(_clientAccounts)) payMiner(minerKey, 500); // if the result map is the real chain state
    }

    private boolean validateAndExecuteCheckBalance(CheckBalanceTransaction transaction,
                                                   Map<String, ClientAccount> resState) {
        return validateCheckBalance(transaction, resState);
    }

    private boolean validateTransfer(TransferTransaction transfer, Map<String, ClientAccount> resState) {
        int amountToTransfer = transfer.getAmount();
        if(amountToTransfer <= 0) {
            transfer.setFailureMessage("Amount to transfer must be > than 0.");
            return false;
        }
        if (!clientAccountExists(transfer.getDestinationAddress(), resState)) {
            Logger.logInfo("ClientAcc: " + resState.size());
            for (Map.Entry<String, ClientAccount> entry : resState.entrySet()) {
                String key = entry.getKey();
                Logger.logInfo("ClientAcc: " + key);
            }
            transfer.setFailureMessage("Receiver account doesn't exist");
            return false;
        }
        if (!clientAccountExists(transfer.getSender(), resState)) {
            transfer.setFailureMessage("Sender account doesn't exist");
            return false;
        }
        if (!resState.get(transfer.getSender()).hasBalanceGreaterThan(amountToTransfer)) {
            transfer.setFailureMessage("Sender doesn't have enough balance");
            return  false;
        }
        return true;
    }

    private void payMiner(String minerKey, int amount) { // TODO transfer from the client account! check if client has the necessary balance
        if(_minerList.contains(minerKey)) _clientAccounts.get(minerKey).deposit(amount);
    }

    private boolean clientAccountExists(String publicKey, Map<String, ClientAccount> resState) {
        if (!resState.containsKey(publicKey) && !_clientAccounts.containsKey(publicKey)) return false;
        if (!resState.containsKey(publicKey) && _clientAccounts.containsKey(publicKey)) {
            resState.put(publicKey, _clientAccounts.get(publicKey).getCopy());
        }
        return true;
    }
}
