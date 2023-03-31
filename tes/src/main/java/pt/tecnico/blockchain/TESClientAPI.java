package pt.tecnico.blockchain;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import pt.tecnico.blockchain.Messages.tes.CheckBalance;
import pt.tecnico.blockchain.Messages.tes.CreateAccount;
import pt.tecnico.blockchain.Messages.tes.TESTransaction;
import pt.tecnico.blockchain.Messages.tes.Transfer;
import pt.tecnico.blockchain.client.BlockchainClientAPI;

public class TESClientAPI {

    private static String contractID = "0"; // TODO define a hash in the server TES class
    private static PublicKey publicKey = BlockchainClientAPI.getPublicKey();
    private static PrivateKey privateKey = BlockchainClientAPI.getPrivateKey();
    private static int nonce = 0;

    synchronized public static void createAccount(int gasPrice, int gasLimit) {
        try {
            CreateAccount txn = new CreateAccount((nonce += 1), Crypto.getHashFromKey(publicKey));
            txn.sign(privateKey);
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    synchronized public static void transfer(PublicKey destination, int amount, int gasPrice, int gasLimit) {
        try {
            Transfer txn = new Transfer((nonce += 1), Crypto.getHashFromKey(publicKey), Crypto.getHashFromKey(destination), amount);
            txn.sign(privateKey);
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    synchronized public static void checkBalance(int gasPrice, int gasLimit) {
        try {
            CheckBalance txn = new CheckBalance((nonce += 1), Crypto.getHashFromKey(publicKey));
            txn.sign(privateKey);
            submitTransactionToBlockchain(txn, gasPrice, gasLimit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void submitTransactionToBlockchain(TESTransaction concreteTxn, int gasPrice, int gasLimit) {
        try {
            BlockchainClientAPI.submitTransaction(KeyConverter.keyToString(publicKey), concreteTxn.getNonce(), concreteTxn, gasPrice, gasLimit, contractID);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
