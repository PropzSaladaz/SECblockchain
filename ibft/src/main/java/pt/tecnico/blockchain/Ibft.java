package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

import java.net.DatagramSocket;
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


    public static void init(DatagramSocket socket, int id, ArrayList<Pair<String, Integer>> members, Application app){
        _pid = id;
        _numProcesses = members.size();
        _app = app;
        IbftMessagehandler.init(socket, members);
    }

    public static Application getApp() {
        return _app;
    }
    //Next Delivery
    public static boolean leader(int consensusInstance, int round) {
        // any deterministic mapping from consensusInstance and round to the identifier of
        //   a process as long as it allows f+1 processes to eventually assume the leader role.
        return 1 == _pid;
    }
     public static boolean checkLeader() {
        return _pid == 1;
    }

    public static void start(Content value) {
        _consensusInstance = _app.getNextInstanceNumber();
        _round = 1;
        _preparedRound = -1;
        _value = value;
        if (leader(_consensusInstance, _round)) {
            System.out.println("IM THE LEADER \n");
            _app.prepareValue(value);
            IbftMessagehandler.doPrePrepare(value);

        }
        IbftTimer.start(_round);
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

    //Quorum Management
    public static int getQuorumMinimumSize() {
        return (_numProcesses + getMaxNumberOfFaultyProcesses()) / 2;
    }

    public static int getMaxNumberOfFaultyProcesses() {
        return (int)Math.floor((_numProcesses-1)/3);
    }

    public static boolean hasPreparedQuorum() {return _prepared.size() > getQuorumMinimumSize();}

    public static void addToPreparedQuorum(ConsensusInstanceMessage message) {_prepared.add(message);}

    public static void addToCommitQuorum(ConsensusInstanceMessage message) {_commited.add(message);}

    public static boolean hasCommitQuorum() {return _commited.size() > getQuorumMinimumSize();}

    public static List<Integer> getCommitQuorum() {
        return _commited.stream().map(ConsensusInstanceMessage::getSenderPID).collect(Collectors.toList());
    }

    public static void endInstance() {
        _prepared.clear();
        _commited.clear();
    }

}
