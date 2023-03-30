package pt.tecnico.blockchain;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.tes.CheckBalance;
import pt.tecnico.blockchain.Messages.tes.CreateAccount;
import pt.tecnico.blockchain.Messages.tes.TESTransaction;
import pt.tecnico.blockchain.Messages.tes.Transfer;
import pt.tecnico.blockchain.client.BlockchainClientAPI;
import pt.tecnico.blockchain.client.DecentralizedAppClientAPI;

public class TESClientAPI implements ContractAPI, DecentralizedAppClientAPI {

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private BlockchainClientAPI client;
    private static String contractID = "0"; // TODO define a hash in the server TES class

    public TESClientAPI(String clientHostname, int clientPort) throws SocketException, UnknownHostException {
        // TODO check if client has key. If not, generate a new pair
        client = new BlockchainClientAPI(clientHostname, clientPort, this);
    }

    @Override
    public void createAccount(int gasPrice, int gasLimit) {
        try {
            CreateAccount txn = new CreateAccount(Crypto.getHashFromKey(publicKey), gasPrice, gasLimit);
            txn.sign(privateKey);
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transfer(PublicKey destination, int amount, int gasPrice, int gasLimit) {
        try {
            Transfer txn = new Transfer(Crypto.getHashFromKey(publicKey), Crypto.getHashFromKey(destination), amount,
                    gasPrice, gasLimit);
            txn.sign(privateKey);
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkBalance(int gasPrice, int gasLimit) {
        try {
            CheckBalance txn = new CheckBalance(Crypto.getHashFromKey(publicKey), gasPrice, gasLimit);
            txn.sign(privateKey);
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
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
        // TODO do something on the client side with the response
    }
}
