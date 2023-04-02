package pt.tecnico.blockchain;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.*;
import pt.tecnico.blockchain.client.BlockchainClientAPI;
import pt.tecnico.blockchain.client.DecentralizedAppClientAPI;

public class TESClientAPI implements DecentralizedAppClientAPI {

    private static String contractID = "0"; // TODO define a hash in the server TES class
    private final BlockchainClientAPI client;
    private final Map<Integer, List<TESTransaction>> tesMessagesQuorum;

    public TESClientAPI(DatagramSocket socket, PublicKey pubKey, PrivateKey privKey) {
        // TODO check if client has key. If not, generate a new pair
        client = new BlockchainClientAPI(socket,  this);
        client.setCredentials(pubKey, privKey);
        tesMessagesQuorum = new HashMap<>();
    }

    public void createAccount(int gasPrice, int gasLimit) {
        try {
            CreateAccount txn = new CreateAccount(client.getNonce(), Crypto.getHashFromKey(client.getPublicKey()));
            txn.sign(client.getPrivateKey());
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void transfer(PublicKey destination, int amount, int gasPrice, int gasLimit) {
        try {
            Transfer txn = new Transfer(client.getNonce(), Crypto.getHashFromKey(client.getPublicKey()),
                    Crypto.getHashFromKey(destination), amount);
            txn.sign(client.getPrivateKey());
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void checkBalance(String readType, int gasPrice, int gasLimit) {
        try {
            CheckBalance txn = new CheckBalance(client.getNonce(), Crypto.getHashFromKey(client.getPublicKey()), readType);
            txn.sign(client.getPrivateKey());
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void waitForMessages() {
        client.waitForMessages();
    }

    private void submitTransactionToBlockchain(TESTransaction concreteTxn, int gasPrice, int gasLimit) {
        try {
            client.submitTransaction(concreteTxn, gasPrice, gasLimit, contractID);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public int getMaxNumberOfFaultyProcesses() {
        return (int)Math.floor((client.getNumberProcesses()-1) / 3.0);
    }

    public boolean hasValidPreparedQuorum(int nonce) {
        return (tesMessagesQuorum.get(nonce).size() == getMaxNumberOfFaultyProcesses() + 1 ) ;
    }


    @Override
    public void deliver(Content message, String status) {
        TESTransaction transaction = (TESTransaction) message;
        if(transaction.checkSign()){
            if (tesMessagesQuorum.containsKey(transaction.getNonce()) && transaction.equals(tesMessagesQuorum.get(transaction.getNonce()).get(0))) tesMessagesQuorum.get(transaction.getNonce()).add(transaction);
            else{
                tesMessagesQuorum.put(transaction.getNonce(),new ArrayList<>());
                tesMessagesQuorum.get(transaction.getNonce()).add(transaction);
            }
        }if(hasValidPreparedQuorum(transaction.getNonce())){
            switch (transaction.getType()) {
                case TESTransaction.CREATE_ACCOUNT:
                    if(status.equals("SUCCESSFUL TRANSACTION")){
                        System.out.println("ACCOUNT CREATED WITH KEY: " + transaction.getSender() +"\n");
                    }else if(status.equals("REJECTED TRANSACTION")){
                        System.out.println("IMPOSSIBLE TO CREATE ACCOUNT WITH KEY: " + transaction.getSender() +"\n");
                    }
                case TESTransaction.TRANSFER:
                    Transfer transfer = (Transfer) transaction;
                    if(status.equals("SUCCESSFUL TRANSACTION")){
                        System.out.println("TRANSFERED " + transfer.getAmount()+ "$" + " TO  " + transaction.getSender() +"\n");
                    }else if(status.equals("REJECTED TRANSACTION")){
                        System.out.println("IMPOSSIBLE TO TRANSFER " + transfer.getAmount()+ "$" + " TO  " + transaction.getSender() +"\n");
                    }
                case TESTransaction.CHECK_BALANCE:
                    CheckBalance checkTransaction = (CheckBalance) transaction;
                    if(status.equals("SUCCESSFUL TRANSACTION")){
                        System.out.println("THE BALANCE IS: " + checkTransaction.getAmount() + "$\n");
                    }else if(status.equals("REJECTED TRANSACTION")){
                        System.out.println("IMPOSSIBLE TO CHECK BALANCE\n");
                    }
                default:
                    System.out.println("ERROR: Could not handle request");
            }
        }

    }
}
