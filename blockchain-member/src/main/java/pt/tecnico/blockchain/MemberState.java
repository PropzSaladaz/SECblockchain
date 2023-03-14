package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Config.*;
import pt.tecnico.blockchain.Messages.*;


import java.lang.Math;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

public class MemberState {
    
    private BlockchainConfig _config;
    private List<ConsensusInstanceMessage> _prepared;
    private List<ConsensusInstanceMessage> _commited;
    private Ibft _ibftInstance = new Ibft();
//    private Blockchain blockchain = new Blockchain();

    public MemberState (BlockchainConfig config) throws NoSuchAlgorithmException {
        _config = config;
    }

    public void startTimer() {
        //_ibftInstance.startTimer();
    }

    public void stopTimer() {
        _ibftInstance.stopTimer();
    }

    public int getNumberOfProcesses() {
        return _config.getNumberOfMemberProcesses();
    }
    
    public int getNumberOfFaultyProcesses() {
        return (int)Math.ceil((this.getNumberOfProcesses()-1)/3);
    }

    public int getQuorumMinimumSize() {
        return (getNumberOfProcesses() + getNumberOfFaultyProcesses()) / 2;
    }

    public void addToPreparedQuorum(ConsensusInstanceMessage message) {
        _prepared.add((ConsensusInstanceMessage)message);
    }

    public boolean hasPreparedQuorum() {
        return _prepared.size() > getQuorumMinimumSize();
    }

    public void addToCommitQuorum(ConsensusInstanceMessage message) {
        _commited.add((ConsensusInstanceMessage)message);
    }

    public boolean hasCommitQuorum() {
        return _prepared.size() > getQuorumMinimumSize();
    }

    public List<Integer> getCommitQuorum() {
        return _commited.stream().map(msg -> msg.getSenderPID()).collect(Collectors.toList());
    }

    public void startIbft(BlockchainMessage inputValue) {
//        _ibftInstance.start(blockchain.getNextBlockNumber(), inputValue);
    }

    public void setIbftPreparedRound(int round) {
        _ibftInstance.setPreparedRound(round);
    }

    public void setIbftPreparedValue(BlockchainMessage value) {
        _ibftInstance.setPreparedValue(value);
    }
}
