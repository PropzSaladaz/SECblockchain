package pt.tecnico.blockchain.behavior.correct;

import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.behavior.correct.DefaultIbftBehavior;

import java.security.NoSuchAlgorithmException;

public abstract class IbftBehavior {

    public abstract String TYPE();

    public void handlePrePrepareRequest(ConsensusInstanceMessage message) {
        DefaultIbftBehavior.handlePrePrepareRequest(message);
    }

    public void handlePrepareRequest(ConsensusInstanceMessage message) {
       DefaultIbftBehavior.handlePrepareRequest(message);
    }

    public void handleCommitRequest(ConsensusInstanceMessage message){
        DefaultIbftBehavior.handleCommitRequest(message);
    }
}
