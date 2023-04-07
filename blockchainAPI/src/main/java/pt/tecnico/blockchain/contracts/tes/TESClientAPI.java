package pt.tecnico.blockchain.contracts.tes;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.KeyConverter;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionType;
import pt.tecnico.blockchain.Messages.blockchain.QuorumSignedBlockMessage;
import pt.tecnico.blockchain.Messages.tes.*;
import pt.tecnico.blockchain.Messages.tes.responses.CheckBalanceResultMessage;
import pt.tecnico.blockchain.Messages.tes.responses.CreateAccountResultMessage;
import pt.tecnico.blockchain.Messages.tes.responses.TESResultMessage;
import pt.tecnico.blockchain.Messages.tes.responses.TransferResultMessage;
import pt.tecnico.blockchain.Messages.tes.transactions.CheckBalanceTransaction;
import pt.tecnico.blockchain.Messages.tes.transactions.CreateAccountTransaction;
import pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction;
import pt.tecnico.blockchain.Messages.tes.transactions.TransferTransaction;
import pt.tecnico.blockchain.client.BlockchainClientAPI;
import pt.tecnico.blockchain.client.DecentralizedAppClientAPI;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

import static pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction.CREATE_ACCOUNT;
import static pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction.TRANSFER;

public class TESClientAPI implements DecentralizedAppClientAPI {

    private static final String contractID = "TESCONTRACTID"; // TODO define a hash in the server TES class
    private final BlockchainClientAPI client;
    private final Map<Integer, Map<Integer, List<TESResultMessage>>> tesMessagesQuorum; // nonce -> hashCode -> List of obj
    private final Set<Integer> deliveredSet;

    public TESClientAPI(DatagramSocket socket, PublicKey pubKey, PrivateKey privKey) {
        client = new BlockchainClientAPI(socket,  this);
        client.setCredentials(pubKey, privKey);
        tesMessagesQuorum = new HashMap<>();
        deliveredSet = new HashSet<>();

    }

    /* -------------------------------------------
     *                  SEND
     * ---------------------------------------- */

    public void createAccount(int gasPrice, int gasLimit) {
        try {
            CreateAccountTransaction txn = new CreateAccountTransaction(
                    client.getNonce(),
                    KeyConverter.keyToString(client.getPublicKey()));
            txn.sign(client.getPrivateKey());
            submitUpdateTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void transfer(PublicKey destination, int amount, int gasPrice, int gasLimit) {
        try {
            TransferTransaction txn = new TransferTransaction(client.getNonce(),
                    KeyConverter.keyToString(client.getPublicKey()),
                    KeyConverter.keyToString(destination), amount);
            txn.sign(client.getPrivateKey());
            submitUpdateTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkBalance(TESReadType readType, int gasPrice, int gasLimit) {
        try {
            CheckBalanceTransaction txn = new CheckBalanceTransaction(client.getNonce(),
                    KeyConverter.keyToString(client.getPublicKey()),
                    readType);
            txn.sign(client.getPrivateKey());
            submitReadTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitUpdateTransactionToBlockchain(TESTransaction concreteTxn, int gasPrice, int gasLimit) {
        try {
            client.submitUpdateTransaction(concreteTxn, gasPrice, gasLimit, contractID);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void submitReadTransactionToBlockchain(TESTransaction concreteTxn, int gasPrice, int gasLimit) {
        try {
            client.submitReadTransaction(concreteTxn, gasPrice, gasLimit, contractID);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /* -------------------------------------------
     *                  RECEIVE
     * ---------------------------------------- */

    public void waitForMessages() {
        client.waitForMessages();
    }

    @Override
    public void deliver(Content message, BlockchainTransactionType operationType, BlockchainTransactionStatus status) {
        TESResultMessage transaction = (TESResultMessage) message; 
        parseTransaction(transaction,  operationType, status);
    }

    private void parseTransaction(TESResultMessage txn, BlockchainTransactionType operationType,
                                  BlockchainTransactionStatus status) {
        switch (operationType) {
            case READ:
                parseRead(txn, status);
                break;
            case UPDATE:
                parseUpdate(txn, status);
                break;
            default:
                break;
        }
    }

    private void parseRead(TESResultMessage txn, BlockchainTransactionStatus status) {
        try {
            CheckBalanceResultMessage txnResponse = (CheckBalanceResultMessage) txn;
            switch (txnResponse.getReadType()) {
                case WEAK:
                    parseWeakRead(txnResponse, status);
                    break;
                case STRONG:
                    parseStrongRead(txnResponse, status);
                    break;
                default:
                    Logger.logWarning("Expected Weak or Strong read type, but got something else.");
                    break;
            }
        } catch (ClassCastException e) {
            Logger.logError("Expected CheckBalance message but got something else");
        } catch (Exception e) {
            Logger.logError("Exception parsing read in TESClientAPI");
        }
    }

    private void parseWeakRead(CheckBalanceResultMessage txn, BlockchainTransactionStatus status) {
        int _currentBalance = ((QuorumSignedBlockMessage) txn.getContent()).assertTesAccountBalance(
                                    KeyConverter.keyToString(client.getPublicKey()), txn.getAmount());
        if (_currentBalance >= 0) {
            printStatus(status,
                    "THE BALANCE IS: " + _currentBalance,
                    "IMPOSSIBLE TO CHECK BALANCE"
            );
        } else {
            Logger.logError("Balance proof was incorrect. ");
        }
    }

    private void parseStrongRead(CheckBalanceResultMessage txn, BlockchainTransactionStatus status) { // wait for f+1 responses
        addTransactionToReceivedMap(txn);
        if (responseReadyToDeliver(txn)) {
            deliveredSet.add(txn.getTxnNonce());
            printStatus(status,
                    "THE BALANCE IS: " + txn.getAmount(),
                    "IMPOSSIBLE TO CHECK BALANCE"
            );
        }
    }

    private void parseUpdate(TESResultMessage txn, BlockchainTransactionStatus status) {
        addTransactionToReceivedMap(txn);
        if (responseReadyToDeliver(txn)) {
            deliveredSet.add(txn.getTxnNonce());
            switch (txn.getType()) {
                case TRANSFER:
                    parseTransfer((TransferResultMessage) txn, status);
                    break;
                case CREATE_ACCOUNT:
                    parseCreateAccount((CreateAccountResultMessage) txn, status);
                    break;
                default:
                    Logger.logWarning("Expected Transfer or Create Balance, but got something else.");
                    break;
            }
        }
    }

    private void addTransactionToReceivedMap(TESResultMessage txn) {
        Integer pid = RSAKeyStoreById.getPidFromPublic(KeyConverter.keyToString(client.getPublicKey()));
        if (txn.verifySignature(pid, txn.getSignature())) {
            tesMessagesQuorum.computeIfAbsent(txn.getTxnNonce(), k -> new HashMap<>());
            Map<Integer, List<TESResultMessage>> receivedWithNonce = tesMessagesQuorum.get(txn.getTxnNonce());
            receivedWithNonce.computeIfAbsent(txn.hashCode(), k -> new ArrayList<>());

            if (receivedWithNonce.containsKey(txn.hashCode())) {
                for (TESResultMessage result : receivedWithNonce.get(txn.hashCode())) {
                    if (result.getResultSender().equals(txn.getResultSender())) {
                        return; // already received msg from this process, so ignore it
                    }
                }
                receivedWithNonce.get(txn.hashCode()).add(txn);
            }
        }
    }

    private boolean responseReadyToDeliver(TESResultMessage txn) {
        return !deliveredSet.contains(txn.getTxnNonce()) && hasMajorityEqualResponses(txn);
    }

    private boolean hasMajorityEqualResponses(TESResultMessage txn) { // wait for f+1 responses
        return getNumberOfResponsesEqualTo(txn) == getMaxNumberOfFaultyProcesses() + 1;
    }

    private int getNumberOfResponsesEqualTo(TESResultMessage txn) {
        return tesMessagesQuorum.get(txn.getTxnNonce()).get(txn.hashCode()).size();
    }

    public int getMaxNumberOfFaultyProcesses() {
        return (int)Math.floor((client.getNumberProcesses()-1) / 3.0);
    }

    private void parseTransfer(TransferResultMessage txn, BlockchainTransactionStatus status) {
        printStatus(status,
                "TRANSFERRED " + txn.getAmount()+ "$" + " TO  " + txn.getDestination() +"\n",
                "IMPOSSIBLE TO TRANSFER " + txn.getAmount()+ "$" + " TO  " + txn.getDestination() +"\n"
        );
    }

    private void parseCreateAccount(CreateAccountResultMessage txn, BlockchainTransactionStatus status) {
        printStatus(status,
                "ACCOUNT CREATED WITH KEY: " + txn.getTransactionInvoker(),
                "IMPOSSIBLE TO CREATE ACCOUNT WITH KEY: " + txn.getTransactionInvoker()
        );
    }

    private void printStatus(BlockchainTransactionStatus status, String successMessage, String failureMessage) {
        switch(status) {
            case VALIDATED:
                Logger.logInfo(successMessage);
                break;
            case REJECTED:
                Logger.logInfo(failureMessage);
                break;
            default:
                break;
        }
    }


}
