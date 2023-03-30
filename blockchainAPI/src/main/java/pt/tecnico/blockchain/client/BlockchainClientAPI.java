package pt.tecnico.blockchain.client;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.blockchain.AppendTransactionReq;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
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

import static pt.tecnico.blockchain.Messages.ApplicationMessage.DECIDE_BLOCK_MESSAGE;

public class BlockchainClientAPI {
    private static List<Pair<String, Integer>> _memberHostNames;
    private DecentralizedAppClientAPI _app;
    private DatagramSocket _socket;
    private String _clientHostname;
    private int _clientPort;

    public BlockchainClientAPI(String clientHostname, int clientPort, DecentralizedAppClientAPI app)
            throws UnknownHostException, SocketException {
        _clientHostname = clientHostname;
        _clientPort = clientPort;
        _app = app;
        _socket = new DatagramSocket(clientPort, InetAddress.getByName(clientHostname));
    }

    public void submitTransaction(UUID id, Content concreteTxn, int gasPrice, int gasLimit, String contractID)
            throws IOException, NoSuchAlgorithmException {
        Content txnRequest = new AppendTransactionReq(new BlockchainTransaction(id, concreteTxn,
                gasPrice, gasLimit, contractID));
        for (Pair<String, Integer> pair : _memberHostNames ) {
            AuthenticatedPerfectLink.send(_socket, txnRequest, pair.getFirst(), pair.getSecond());
        }
    }

    public static void setMembers(ArrayList<Pair<String, Integer>> memberHostNames) {
        _memberHostNames = memberHostNames;
    }

    public void waitForMessages() {
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

    private void handleResponse(Content message) {
        ApplicationMessage msg = (ApplicationMessage) message;
        if (msg.getApplicationMessageType().equals(DECIDE_BLOCK_MESSAGE)) {
            try {
                DecideBlockMessage decidedBlock = (DecideBlockMessage) message;
                if (verifyQuorumSignatures(decidedBlock.getQuorum())) {
                    _app.deliver(decidedBlock.getContent());
                } else {
                    Logger.logWarning("Could not verify signatures. Invalid response");
                }
            } catch(IOException | SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("ERROR: Could not handle request");
        }
    }
}
