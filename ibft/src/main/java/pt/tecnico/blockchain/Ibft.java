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
            System.out.println("IBFT: free to start");
            startNewInstance(value);
        } else { // Instance already active
            System.out.println("IBFT: Busy now, put in queue");
            addToQueue(value);
        }
    }

    private static void startNewInstance(Content value) {
        System.out.println("IBFT: Starting new instance");
        _consensusInstance = _app.getNextInstanceNumber();
        _round = 1;
        _preparedRound = -1;
        _value = value;
        if (leader(_consensusInstance, _round) == _pid) {
            System.out.println("IM THE LEADER \n");
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
        return _prepared.size() > getQuorumMinimumSize() && verifyQuorumSignatures(_prepared, _prepared.size());
    }

    public static void addToPreparedQuorum(ConsensusInstanceMessage message) { 
        if (!quorumContainsPID(_prepared, _pid)) {
            _prepared.add(message);
        } else {
           System.out.println("ERROR: Multiple messages of PREAPRE of same instance from process: " + message.getSenderPID());
        }    
    }
    
    public static void addToCommitQuorum(ConsensusInstanceMessage message) {
        if (!quorumContainsPID(_commited, _pid)) {
            _commited.add(message);
        } else {
           System.out.println("ERROR: Multiple messages of PREAPRE of same instance from process: " + message.getSenderPID());
        }    
    }

    public static boolean quorumContainsPID(List<ConsensusInstanceMessage> quorum, Integer pid) {
        return getQuorumPIDs(quorum).contains(pid);
    }

    public static boolean hasValidCommitQuorum() {
        return _commited.size() > getQuorumMinimumSize() && verifyQuorumSignatures(_commited, _commited.size());
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

    public static List<Integer> getQuorumPIDs(List<ConsensusInstanceMessage> quorum) {
        return quorum.stream().map(ConsensusInstanceMessage::getSenderPID).collect(Collectors.toList());
    }

    public static List<ConsensusInstanceMessage> getCommitQuorum() {
        return _commited;
    }
    public static List<ConsensusInstanceMessage> getPreparedQuorum() {
        return _prepared;
    }

    public static void endInstance() {
        _prepared.clear();
        _commited.clear();
        if (hasMessageInQueue()) {
            System.out.println("IBFT: fetching from queue to start a new instance");
            Ibft.startNewInstance(_messageQueue.remove(_messageQueue.size() - 1));
        } else {
            _decidingInstance = false;
        }
    }
}
