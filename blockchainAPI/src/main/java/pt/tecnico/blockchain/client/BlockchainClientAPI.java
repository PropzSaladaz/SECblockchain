package pt.tecnico.blockchain.client;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;

import java.io.IOException;
import java.net.DatagramSocket;
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

public class BlockchainClientAPI {
    private static List<Pair<String, Integer>> _memberHostNames;
    private static DatagramSocket _socket;
    private static Pair<PublicKey,PrivateKey> _clientKeys;

    public static void setSocket(DatagramSocket socket) {
        _socket = socket;
    }

    public static void setCredentials(PublicKey publicKey, PrivateKey privateKey) {
        _clientKeys = new Pair<PublicKey, PrivateKey>(publicKey, privateKey);
    }

    public static void submitTransaction(String from, int nonce, Content concreteTxn, int gasPrice, int gasLimit, String contractID)
            throws IOException, NoSuchAlgorithmException {
        Content txnRequest = new BlockchainTransaction(from, nonce, concreteTxn, gasPrice, gasLimit, contractID);
        for (Pair<String, Integer> pair : _memberHostNames ) {
            AuthenticatedPerfectLink.send(_socket, txnRequest, pair.getFirst(), pair.getSecond());
        }
    }

    public static PublicKey getPublicKey() {
        return _clientKeys.getFirst();
    }

    public static PrivateKey getPrivateKey() {
        return _clientKeys.getSecond();
    }

    public static Pair<PublicKey,PrivateKey> getClientKeys() {
        return _clientKeys;
    }

    public static void setMembers(ArrayList<Pair<String, Integer>> memberHostNames) {
        _memberHostNames = memberHostNames;
    }

    public static void waitForMessages() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    Content message = AuthenticatedPerfectLink.deliver(_socket);
                    handleResponse(message);
                } catch (ClassCastException | IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                    System.out.println("Received a corrupted message, ignoring...");
                }
            }
        });
        worker.start();
    }

    /*
    private boolean verifyQuorumSignatures(List<ConsensusInstanceMessage> quorum)
            throws IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {

        for (ConsensusInstanceMessage consensusMessage: quorum){
            if (!Crypto.verifySignature(
                    MessageManager.getContentBytes(consensusMessage.getContent()),
                    consensusMessage.getSignatureBytes(),
                    RSAKeyStoreById.getPublicKey(consensusMessage.getSenderPID()))){
                return false;
            }
        }        
        return true;
    }
     */

    private static void handleResponse(Content message) {
        ApplicationMessage msg = (ApplicationMessage) message;
        switch (msg.getApplicationMessageType()) {
            case ApplicationMessage.BLOCKCHAIN_TRANSACTION_MESSAGE:
                break;
            default:
                System.out.println("ERROR: Could not handle request");
                break;
        }
    }
}
