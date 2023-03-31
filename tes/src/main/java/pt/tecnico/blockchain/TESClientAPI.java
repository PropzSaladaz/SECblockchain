package pt.tecnico.blockchain;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.CheckBalance;
import pt.tecnico.blockchain.Messages.tes.CreateAccount;
import pt.tecnico.blockchain.Messages.tes.TESTransaction;
import pt.tecnico.blockchain.Messages.tes.Transfer;
import pt.tecnico.blockchain.client.BlockchainClientAPI;
import pt.tecnico.blockchain.client.DecentralizedAppClientAPI;

public class TESClientAPI implements DecentralizedAppClientAPI {

    private static String contractID = "0"; // TODO define a hash in the server TES class
    private final BlockchainClientAPI client;

    public TESClientAPI(DatagramSocket socket, PublicKey pubKey, PrivateKey privKey) {
        // TODO check if client has key. If not, generate a new pair
        client = new BlockchainClientAPI(socket,  this);
        client.setCredentials(pubKey, privKey);
    }

    public void createAccount(int gasPrice, int gasLimit) {
        try {
            CreateAccount txn = new CreateAccount(Crypto.getHashFromKey(client.getPublicKey()));
            txn.sign(client.getPrivateKey());
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void transfer(PublicKey destination, int amount, int gasPrice, int gasLimit) {
        try {
            Transfer txn = new Transfer(Crypto.getHashFromKey(client.getPublicKey()),
                    Crypto.getHashFromKey(destination), amount);
            txn.sign(client.getPrivateKey());
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void checkBalance(int gasPrice, int gasLimit) {
        try {
            CheckBalance txn = new CheckBalance(Crypto.getHashFromKey(client.getPublicKey()));
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
            client.submitTransaction(concreteTxn.getId(), concreteTxn, gasPrice, gasLimit, contractID);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliver(Content message) {
        // Deliver msg
    }
}
