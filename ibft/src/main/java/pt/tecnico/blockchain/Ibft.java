package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Ibft {

    private static int _pid;
    private static int _numProcesses;
    private static Application _app;
    private static int _consensusInstance;
    private static int _round;
    private static int _preparedRound;
    private static Content _preparedValue;
    private static List<ConsensusInstanceMessage> _prepared = new ArrayList<>();
    private static List<ConsensusInstanceMessage> _commited = new ArrayList<>();
    private static List<Content> _messageQueue = new ArrayList<>();
    private static boolean _decidingInstance;

    public static void init(DatagramSocket socket, int id, ArrayList<Pair<String, Integer>> members, Application app){
        _pid = id;
        _numProcesses = members.size();
        _app = app;
        IbftMessagehandler.init(socket, members, _pid);

    }

    public static Application getApp() {
        return _app;
    }

    public static int leader() {return 1;}
     
    public synchronized static void start(Content value) {
        if (tryDecideNewInstance()) {
            startNewInstance(value);
        } else { // Instance already active either because of a PREPARE , COMMIT or client message
            // there are cases where a PREPARE comes before a client message, and we fall in this condition
            addToQueueIfNotStartedAlready(value);
        }
    }

    private synchronized static void startNewInstance(Content value) {
        _round = 1;
        _preparedRound = -1;
        if (leader() == _pid) {
            _app.prepareValue(value);
            IbftMessagehandler.broadcastPrePrepare(value);
        }
        IbftTimer.start(_round);
    }

    public static synchronized boolean tryDecideNewInstance() {
        if (!_decidingInstance) {
            _decidingInstance = true;
            return true;
        }
        return false;
    }

    public static synchronized boolean hasMessageInQueue() {
        return _messageQueue.size() > 0;
    }

    private static synchronized void addToQueueIfNotStartedAlready(Content value) {
        if (valueIsNotInPreparedNorInCommitted(value)) _messageQueue.add(value);
    }

    private static boolean valueIsNotInPreparedNorInCommitted(Content value) {
        if (_prepared.size() > 0 && _prepared.get(0).equals(value)) return false;
        if (_commited.size() > 0 && _commited.get(0).equals(value)) return false;
        return true;
    }

    public static int getPid() {
        return _pid;
    }

    public static int getConsensusInstance() {
        return _consensusInstance;
    }

    public static int getRound() {
        return _round;
    }

    public static synchronized void setPreparedRound(int round) {
        _preparedRound = round;
    }

    public static synchronized void setPreparedValue(Content value) {
        _preparedValue = value;
    }

    public static void handleMessage(ConsensusInstanceMessage ibftMessage, int actualSenderID) throws NoSuchAlgorithmException {
        IbftMessagehandler.handleMessage(ibftMessage, actualSenderID);
    }

    public static int getQuorumMinimumSize() {
        return (int)Math.floor((_numProcesses + getMaxNumberOfFaultyProcesses()) / 2.0);
    }

    public static int getMaxNumberOfFaultyProcesses() {
        return (int)Math.floor((_numProcesses-1) / 3.0);
    }

    public static synchronized boolean hasValidPreparedQuorum() {
        return (_prepared.size() == getQuorumMinimumSize() + 1 ) && verifyQuorumSignatures(_prepared, _prepared.size()) && checkPrepareQuorumContent();
    }

    public static synchronized void addToPreparedQuorum(ConsensusInstanceMessage message) {
        if (!quorumContainsPID(_prepared, message.getSenderPID())) {
            if (message.getConsensusInstance() == _consensusInstance){
                Logger.logDebug("Added PREPARE to prepare quorum");
                _prepared.add(message);
            } else Logger.logWarning("Received a PREPARE message for a wrong instance. Expected:" + _consensusInstance
                    + "got: " + message.getConsensusInstance());
        } else Logger.logWarning("Multiple messages of PREPARE of same instance from process: "
                + message.getSenderPID());
    }
    
    public static synchronized void addToCommitQuorum(ConsensusInstanceMessage message) {
        if (!quorumContainsPID(_commited, message.getSenderPID())) {
            if(message.getConsensusInstance() == _consensusInstance) {
                Logger.logDebug("Added COMMIT to prepare quorum");
                _commited.add(message);
            } else Logger.logWarning("Received a COMMIT message for a wrong instance. Expected:" + _consensusInstance
                    + "got: " + message.getConsensusInstance());
        } else System.out.println("Multiple messages of COMMIT of same instance from process: "
                + message.getSenderPID());
    }

    public synchronized static boolean hasSamePreparedValue(ConsensusInstanceMessage m) {
        return _preparedValue != null && m.getContent().equals(_preparedValue);
    }

    public static boolean quorumContainsPID(List<ConsensusInstanceMessage> quorum, Integer pid) {
        return getQuorumPIDs(quorum).contains(pid);
    }

    public static synchronized boolean hasValidCommitQuorum() {
        return ( _commited.size() == getQuorumMinimumSize() + 1 ) && verifyQuorumSignatures(_commited, _commited.size());
    }

    public static boolean checkPrepareQuorumContent(){
        ConsensusInstanceMessage message = null;
        for(ConsensusInstanceMessage consensusMessage: _prepared){
            if(message == null) message = consensusMessage;
            if(!message.getContent().equals(consensusMessage.getContent())) return false;
        }
        return true;
    }

    public static boolean verifyQuorumSignatures(List<ConsensusInstanceMessage> quorum, int quorumSize) {
        try {
            if(quorum.size() == quorumSize){
                List<ConsensusInstanceMessage> verifiedQuorum = quorum.stream().filter(msg -> {
                    try {
                        return Crypto.verifySignature(
                                MessageManager.getContentBytes(msg.getContent()),
                                msg.getSignatureBytes(),
                                RSAKeyStoreById.getPublicKey(msg.getSenderPID()));

                    } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException | IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }).collect(Collectors.toList());
                return verifiedQuorum.size() == quorumSize;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized  List<Integer> getQuorumPIDs(List<ConsensusInstanceMessage> quorum) {
        return quorum.stream().map(ConsensusInstanceMessage::getSenderPID).collect(Collectors.toList());
    }

    public synchronized static List<Content> getCommitQuorum() {
        return new ArrayList<Content>(_commited);
    }
    public synchronized static List<Content> getPreparedQuorum() {
        return new ArrayList<Content>(_prepared);
    }

    public static synchronized void endInstance() {
        Logger.logDebug("Ending instance");
        clearQuorums();
        setPreparedValue(null);
        _consensusInstance = _app.getNextInstanceNumber();
        if (hasMessageInQueue()) {
            Ibft.startNewInstance(_messageQueue.remove(_messageQueue.size() - 1));
        } else {
            _decidingInstance = false;
        }
    }

    private static synchronized void clearQuorums() {
        _prepared.clear();
        _commited.clear();
    }
}
