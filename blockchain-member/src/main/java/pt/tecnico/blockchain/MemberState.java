package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Config.*;
import pt.tecnico.blockchain.Messages.*;


import java.lang.Math;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemberState {

    private BlockchainConfig _config;
    private List<ConsensusInstanceMessage> _prepared = new ArrayList<>();
    private List<ConsensusInstanceMessage> _commited = new ArrayList<>();
    private Ibft _ibftInstance;
    private Blockchain blockchain = new Blockchain();
    private int _id;

    public MemberState (BlockchainConfig config,int id) throws NoSuchAlgorithmException {
        _config = config;
        _id = id;
        _ibftInstance = new Ibft(_id);
    }

    //Timer Methods
    /*public void startTimer() {
        _ibftInstance.startTimer();
    }
    public void stopTimer() {
        _ibftInstance.stopTimer();
    }*/
    public int getNumberOfFaultyProcesses() {
        int  numberProcesses = _config.getNumberOfMemberProcesses();
        return (int)Math.ceil((numberProcesses-1)/3);
    }

    public boolean checkLeader(){return _ibftInstance.checkLeader(_id);}

    public int getConsensusInstance() {return _ibftInstance.getConsensusInstance();}

    public int getRound() {return _ibftInstance.getRound();}

    public int getPid() {return _id;}

    public String getLastBlockHash(){return blockchain.getLastBlockHash();}

    public boolean verifyNewBlock(String message,String digest) throws NoSuchAlgorithmException { return blockchain.verifyChainUpdated(message,digest);}

    public void addNewBlock(BlockchainMessage message) throws NoSuchAlgorithmException { blockchain.addBlock(message);}
    //Quorum Management
    public int getQuorumMinimumSize() {
        return (_config.getNumberOfMemberProcesses() + getNumberOfFaultyProcesses()) / 2;
    }
    public boolean hasPreparedQuorum() {return _prepared.size() > getQuorumMinimumSize();}

    public void addToPreparedQuorum(ConsensusInstanceMessage message) {_prepared.add(message);}

    public void addToCommitQuorum(ConsensusInstanceMessage message) {_commited.add(message);}

    public boolean hasCommitQuorum() {return _commited.size() > getQuorumMinimumSize();}

    public List<Integer> getCommitQuorum() {
        return _commited.stream().map(msg -> msg.getSenderPID()).collect(Collectors.toList());
    }
    //Start and Sets Methods
    public void startIbft(BlockchainMessage message) {
        _ibftInstance.start(blockchain.getNextBlockNumber(), message);

    }

    public void setIbftPreparedRound(int round) {_ibftInstance.setPreparedRound(round);}

    public void setIbftPreparedValue(BlockchainMessage value) {_ibftInstance.setPreparedValue(value);}

}
