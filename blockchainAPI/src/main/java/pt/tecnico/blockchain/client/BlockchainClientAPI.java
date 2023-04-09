package pt.tecnico.blockchain.client;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.KeyConverter;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionType;
import pt.tecnico.blockchain.Messages.blockchain.TransactionResultMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.links.APLReturnMessage;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.security.PublicKey;
import java.security.PrivateKey;

import static pt.tecnico.blockchain.Messages.ApplicationMessage.TRANSACTION_RESULT_MESSAGE;

public class BlockchainClientAPI {
    private static List<Pair<String, Integer>> _memberHostNames;
    private final DatagramSocket _socket;
    private final DecentralizedAppClientAPI _app;
    private Pair<PublicKey,PrivateKey> _clientKeys;
    private int nonce = 0;

    public BlockchainClientAPI(DatagramSocket socket, DecentralizedAppClientAPI app) {
        _app = app;
        _socket = socket;
    }

    public void setCredentials(PublicKey publicKey, PrivateKey privateKey) {
        _clientKeys = new Pair<>(publicKey, privateKey);
    }

    public static void setMembers(ArrayList<Pair<String, Integer>> memberHostNames) {
        _memberHostNames = memberHostNames;
    }

    public Integer getNonce() {
        return nonce;
    }

    synchronized public void submitUpdateTransaction(Content concreteTxn, int gasPrice, int gasLimit, String contractID)
            throws IOException, NoSuchAlgorithmException {
        BlockchainTransaction txn = buildBlockchainTransaction(concreteTxn, gasPrice, gasLimit, contractID);
        txn.setOperationType(BlockchainTransactionType.UPDATE);
        sendTransactionToMembers(txn);
    }

    synchronized public void submitReadTransaction(Content concreteTxn, int gasPrice, int gasLimit, String contractID)
            throws IOException, NoSuchAlgorithmException {
        BlockchainTransaction txn = buildBlockchainTransaction(concreteTxn, gasPrice, gasLimit, contractID);
        txn.setOperationType(BlockchainTransactionType.READ);
        sendTransactionToMembers(txn);
    }

    private BlockchainTransaction buildBlockchainTransaction(Content concreteTxn, int gasPrice, int gasLimit, String contractID) {
        String from = KeyConverter.keyToString(_clientKeys.getFirst());
        return new BlockchainTransaction(from, getNonceAndIncrease(), concreteTxn, gasPrice, gasLimit, contractID);
    }

    private void sendTransactionToMembers(BlockchainTransaction txnRequest) throws IOException, NoSuchAlgorithmException {
        for (Pair<String, Integer> pair : _memberHostNames ) {
            AuthenticatedPerfectLink.send(_socket, txnRequest, pair.getFirst(), pair.getSecond());
        }
    }

    private synchronized int getNonceAndIncrease() {
        return nonce++;
    }

    public PublicKey getPublicKey() {
        return _clientKeys.getFirst();
    }

    public int getNumberProcesses() {
        return _memberHostNames.size();
    }

    public PrivateKey getPrivateKey() {
        return _clientKeys.getSecond();
    }

    public void waitForMessages() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    APLReturnMessage message = AuthenticatedPerfectLink.deliver(_socket);
                    handleResponse(message.getContent());
                } catch (ClassCastException | IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                    Logger.logWarning("Received a corrupted message, ignoring...", e);
                }
            }
        });
        worker.start();
    }

    private void handleResponse(Content message) {
        if (responseIsTransactionResult(message)) {
            TransactionResultMessage transactionResult = (TransactionResultMessage) message;
            _app.deliver(transactionResult.getContent(),
                    transactionResult.getTransactionType(),
                    transactionResult.getStatus());
        } else {
            Logger.logWarning("Expected to receive a TransactionResultMessage but received something else.");
        }
    }

    private boolean responseIsTransactionResult(Content message) {
        try {
            ApplicationMessage msg = (ApplicationMessage) message;
            return msg.getApplicationMessageType().equals(TRANSACTION_RESULT_MESSAGE);
        } catch (ClassCastException e) {
            return false;
        }

    }
}
