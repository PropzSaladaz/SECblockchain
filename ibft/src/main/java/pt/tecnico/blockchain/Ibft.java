package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
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
    private static Content _value;
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

    public static int leader(int consensusInstance, int round) {
        // any deterministic mapping from consensusInstance and round to the identifier of
        //   a process as long as it allows f+1 processes to eventually assume the leader role.
        return 1;
    }
     
    public static void start(Content value) {
        if (tryDecideNewInstance()) {
//            System.out.println("IBFT: free to start");
            startNewInstance(value);
        } else { // Instance already active
//            System.out.println("IBFT: Busy now, put in queue");
            addToQueue(value);
        }
    }

    private static void startNewInstance(Content value) {
//        System.out.println("IBFT: Starting new instance");
        _consensusInstance = _app.getNextInstanceNumber();
        _round = 1;
        _preparedRound = -1;
        _value = value;
        if (leader(_consensusInstance, _round) == _pid) {
//            System.out.println("IM THE LEADER \n");
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

    public static synchronized void addToQueue(Content value) {
        _messageQueue.add(value);
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

    public void setRound(int round) {
        _round = round;
    }

    public int getPreparedRound() {
        return _preparedRound;
    }

    public static void setPreparedRound(int round) {
        _preparedRound = round;
    }

    public Content getPreparedValue() {
        return _preparedValue;
    }

    public static void setPreparedValue(Content value) {
        _preparedValue = value;
    }

    public static void handleMessage(ConsensusInstanceMessage ibftMessage) {
        IbftMessagehandler.handleMessage(ibftMessage);
    }

    public Content getValue() {
        return _value;
    }

    public static int getQuorumMinimumSize() {
        return (int)Math.floor(_numProcesses + getMaxNumberOfFaultyProcesses()) / 2;
    }

    public static int getMaxNumberOfFaultyProcesses() {
        return (int)Math.floor((_numProcesses-1)/3);
    }

    public static boolean hasValidPreparedQuorum() {
//        System.out.println("Size: " + _prepared.size());
//        System.out.println("Quorum size: " + (int)(getQuorumMinimumSize() + 1));
        return (_prepared.size() == getQuorumMinimumSize() + 1 ) && verifyQuorumSignatures(_prepared, _prepared.size());
    }

    public static synchronized void addToPreparedQuorum(ConsensusInstanceMessage message) {
//        System.out.println("mesagePID: " + message.getSenderPID());
//        System.out.println("PreparedQuorum: " + _prepared);
        if (!quorumContainsPID(_prepared, message.getSenderPID()) && message.getConsensusInstance() == _consensusInstance) {
            _prepared.add(message);
        } else {
           System.out.println("INFO: Multiple messages of PREPARE of same instance from process: " + message.getSenderPID());
        }    
    }
    
    public static synchronized void addToCommitQuorum(ConsensusInstanceMessage message) {
        if (!quorumContainsPID(_commited, message.getSenderPID()) && message.getConsensusInstance() == _consensusInstance) {
//            System.out.println("Adding to commited " + message);
//            System.out.println("Current instance state: " + _decidingInstance);
//            System.out.println("Instance: " + _consensusInstance);
            _commited.add(message);
        } else {
           System.out.println("INFO: Multiple messages of COMMIT of same instance from process: " + message.getSenderPID());
        }    
    }

    public static boolean quorumContainsPID(List<ConsensusInstanceMessage> quorum, Integer pid) {
        return getQuorumPIDs(quorum).contains(pid);
    }

    public static boolean hasValidCommitQuorum() {
        return ( _commited.size() == getQuorumMinimumSize() + 1 ) && verifyQuorumSignatures(_commited, _commited.size());
    }

    public static boolean verifyQuorumSignatures(List<ConsensusInstanceMessage> quorum, int quorumSize) {
//        System.out.println(" quorum");
        try {
            if(quorum.size() == quorumSize){
                List<ConsensusInstanceMessage> verifiedQuorum = quorum.stream().filter(msg -> {
                    try {
//                        System.out.println("exiting0");
                        boolean value = Crypto.verifySignature(
                                MessageManager.getContentBytes(msg.getContent()),
                                msg.getSignatureBytes(),
                                RSAKeyStoreById.getPublicKey(msg.getSenderPID()));
//                        System.out.println("Verifying sig: " + value);
                        return value;

                    } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException | IOException e) {
                        e.printStackTrace();
//                        System.out.println("exiting1");
                        return false;
                    }
                }).collect(Collectors.toList());

                return verifiedQuorum.size() == quorumSize;
            }else{
//                System.out.println("exiting2");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("exiting3");
            return false;
        }
    }

    public static List<Integer> getQuorumPIDs(List<ConsensusInstanceMessage> quorum) {
        return quorum.stream().map(ConsensusInstanceMessage::getSenderPID).collect(Collectors.toList());
    }

    public static List<ConsensusInstanceMessage> getCommitQuorum() {
        return new ArrayList<ConsensusInstanceMessage>(_commited);
    }
    public static List<ConsensusInstanceMessage> getPreparedQuorum() {
        return new ArrayList<ConsensusInstanceMessage>(_prepared);
    }

    public static synchronized void endInstance() {
        _prepared.clear();
        _commited.clear();
        if (hasMessageInQueue()) {
//            System.out.println("IBFT: fetching from queue to start a new instance");
            Ibft.startNewInstance(_messageQueue.remove(_messageQueue.size() - 1));
        } else {
            _decidingInstance = false;
        }
    }
}
