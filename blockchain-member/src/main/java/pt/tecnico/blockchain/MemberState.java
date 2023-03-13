package pt.tecnico.blockchain;

import pt.tecnico.blockchain.SlotTimer.*;
import pt.tecnico.blockchain.Config.*;
import pt.tecnico.blockchain.Messages.*;

import java.lang.Math;
import java.util.List;

public class MemberState {
    
    private SlotTimer _timer;
    private BlockchainConfig _config;
    private List<ConsensusInstanceMessage> _prepared;
    private List<ConsensusInstanceMessage> _commited;
    private MemberFrontend _memberFrontend;

    public MemberState (BlockchainConfig config, SlotTimer timer, MemberFrontend memberFrontend) {
        _timer = timer;
        _config = config;
        _memberFrontend = memberFrontend;
    }

    public void startTimer() {
        _timer.start();
    }

    public void stopTimer() {
        _timer.stop();
    }

    public int getNumberOfProcesses() {
        return _config.getNumberOfMemberProcesses();
    }
    
    public int getNumberOfFaultyProcesses() {
        return (int)Math.ceil((this.getNumberOfProcesses()-1)/3);
    }

    public void addToCommitQuorum(ConsensusInstanceMessage message) {
    }

    public void addToPreparedQuorum(ConsensusInstanceMessage message) {
        
    }
}
