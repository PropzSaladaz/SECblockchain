package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

public class Ibft {

    private int _pid;
    private int _consensusInstance;
    private int _round;
    private int _preparedRound;
    private BlockchainMessage _preparedValue;
    private BlockchainMessage _value;
    //private IbftTimer timer = new IbftTimer();


    public Ibft(int id){_pid = id;}
    //Next Delivery
    public boolean leader(int consensusInstance, int round) {
        // any deterministic mapping from consensusInstance and round to the identifier of
        //   a process as long as it allows f+1 processes to eventually assume the leader role.
        return 1 == _pid;
    }
     public boolean checkLeader(int pid) {
        // any deterministic mapping from consensusInstance and round to the identifier of
        //   a process as long as it allows f+1 processes to eventually assume the leader role.
        return pid == 1;
    }

    public void start(int consensusInstance,BlockchainMessage value) {
        _consensusInstance = consensusInstance;
        _round = 1;
        _preparedRound = -1;
        _value = value;
        if (leader(_consensusInstance, _round)) {
            System.out.println("IM THE LEADER \n");
        }
        //startTimer(_round);
    }

    /*public void startTimer(int round) {
        timer.start(round);
    }

    public void stopTimer() {
        timer.stop();
    }*/

    public int getPID() {
        return _pid;
    }

    public int getConsensusInstance() {
        return _consensusInstance;
    }

    public int getRound() {
        return _round;
    }

    public void setRound(int round) {
        _round = round;
    }

    public int getPreparedRound() {
        return _preparedRound;
    }

    public void setPreparedRound(int round) {
        _preparedRound = round;
    }

    public BlockchainMessage getPreparedValue() {
        return _preparedValue;
    }

    public void setPreparedValue(BlockchainMessage value) {
        _preparedValue = value;
    }

    public BlockchainMessage getValue() {
        return _value;
    }

}
