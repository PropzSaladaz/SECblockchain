package pt.tecnico.blockchain.contracts.tes;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionType;
import pt.tecnico.blockchain.Messages.tes.*;
import pt.tecnico.blockchain.client.BlockchainClientAPI;
import pt.tecnico.blockchain.client.DecentralizedAppClientAPI;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static pt.tecnico.blockchain.Messages.tes.TESTransaction.CREATE_ACCOUNT;
import static pt.tecnico.blockchain.Messages.tes.TESTransaction.TRANSFER;

public class TESClientAPI implements DecentralizedAppClientAPI {

    private static final String contractID = "HARDCODEDCONTRACID"; // TODO define a hash in the server TES class
    private final BlockchainClientAPI client;
    private final Map<Integer, Map<Integer, List<TESTransaction>>> tesMessagesQuorum; // nonce -> hashCode -> List of obj
    private final Set<Integer> deliveredSet;

    public TESClientAPI(DatagramSocket socket, PublicKey pubKey, PrivateKey privKey) {
        // TODO check if client has key. If not, generate a new pair
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
            CreateAccount txn = new CreateAccount(client.getNonce(), Crypto.getHashFromKey(client.getPublicKey()));
            txn.sign(client.getPrivateKey());
            submitUpdateTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void transfer(PublicKey destination, int amount, int gasPrice, int gasLimit) {
        try {
            Transfer txn = new Transfer(client.getNonce(),
                    Crypto.getHashFromKey(client.getPublicKey()),
                    Crypto.getHashFromKey(destination), amount);
            txn.sign(client.getPrivateKey());
            submitUpdateTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void checkBalance(TESReadType readType, int gasPrice, int gasLimit) {
        try {
            CheckBalance txn = new CheckBalance(client.getNonce(),
                    Crypto.getHashFromKey(client.getPublicKey()), readType);
            txn.sign(client.getPrivateKey());
            submitReadTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
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
        TESTransaction transaction = (TESTransaction) message; // TODO maybe create a class just for the response to the client
        if (transaction.checkSignature()) parseTransaction(transaction,  operationType, status);
        else Logger.logWarning("Received a TES response with invalid signature");
    }

    private void parseTransaction(TESTransaction txn, BlockchainTransactionType operationType,
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

    private void parseRead(TESTransaction txn, BlockchainTransactionStatus status) {
        try {
            CheckBalance balance = (CheckBalance) txn;
            switch (balance.getReadType()) {
                case WEAK:
                    parseWeakRead(balance, status);
                    break;
                case STRONG:
                    parseStrongRead(balance, status);
                    break;
                default:
                    Logger.logWarning("Expected Weak or Strong read type, but got something else.");
                    break;
            }
        } catch (ClassCastException e) {
            Logger.logError("Expected CheckBalance message but got something else");
        }
    }

    private void parseWeakRead(CheckBalance txn, BlockchainTransactionStatus status) {
        // TODO validate the received signature quorum, etc...
    }

    private void parseStrongRead(CheckBalance txn, BlockchainTransactionStatus status) { // wait for f+1 responses
        addTransactionToReceivedMap(txn);
        if (responseReadyToDeliver(txn)) {
            deliveredSet.add(txn.getNonce());
            printStatus(status,
                    "THE BALANCE IS: " + txn.getAmount(),
                    "IMPOSSIBLE TO CHECK BALANCE"
                    );
        }
    }

    private void parseUpdate(TESTransaction txn, BlockchainTransactionStatus status) {
        addTransactionToReceivedMap(txn);
        if (responseReadyToDeliver(txn)) {
            deliveredSet.add(txn.getNonce());
            switch (txn.getType()) {
                case TRANSFER:
                    parseTransfer((Transfer) txn, status);
                    break;
                case CREATE_ACCOUNT:
                    parseCreateAccount((CreateAccount) txn, status);
                    break;
                default:
                    Logger.logWarning("Expected Transfer or Create Balance, but got something else.");
                    break;
            }
        }
    }

    private void addTransactionToReceivedMap(TESTransaction txn) {
        tesMessagesQuorum.computeIfAbsent(txn.getNonce(), k -> new HashMap<>());
        Map<Integer, List<TESTransaction>> receivedWithNonce = tesMessagesQuorum.get(txn.getNonce());
        receivedWithNonce.computeIfAbsent(txn.hashCode(), k -> new ArrayList<>());
        receivedWithNonce.get(txn.hashCode()).add(txn);
    }

    private boolean responseReadyToDeliver(TESTransaction txn) {
        return !deliveredSet.contains(txn.getNonce()) && hasMajorityEqualResponses(txn);
    }

    private boolean hasMajorityEqualResponses(TESTransaction txn) { // wait for f+1 responses
        return getNumberOfResponsesEqualTo(txn) == getMaxNumberOfFaultyProcesses() + 1;
    }

    private int getNumberOfResponsesEqualTo(TESTransaction txn) {
        return tesMessagesQuorum.get(txn.getNonce()).get(txn.hashCode()).size();
    }

    public int getMaxNumberOfFaultyProcesses() {
        return (int)Math.floor((client.getNumberProcesses()-1) / 3.0);
    }

    private void parseTransfer(Transfer txn, BlockchainTransactionStatus status) {
        printStatus(status,
                "TRANSFERRED " + txn.getAmount()+ "$" + " TO  " + txn.getSender() +"\n",
                "IMPOSSIBLE TO TRANSFER " + txn.getAmount()+ "$" + " TO  " + txn.getSender() +"\n"
        );
    }

    private void parseCreateAccount(CreateAccount txn, BlockchainTransactionStatus status) {
        printStatus(status,
                "ACCOUNT CREATED WITH KEY: " + txn.getSender(),
                "IMPOSSIBLE TO CREATE ACCOUNT WITH KEY: " + txn.getSender()
        );
    }

    private void printStatus(BlockchainTransactionStatus status, String successMessage, String failureMessage) {
        switch(status) {
            case SUCCESS:
                Logger.logInfo(successMessage);
                break;
            case FAILURE:
                Logger.logInfo(failureMessage);
                break;
            default:
                break;
        }
    }


}
