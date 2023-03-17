package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

import java.util.List;

public class DecideBlockMessage extends ApplicationMessage implements Content {

    private int _consensusInstance;
    private List<ConsensusInstanceMessage> _quorum;

    public DecideBlockMessage(int consensusInstance, Content message, List<ConsensusInstanceMessage> quorum){
        super(message);
        _consensusInstance = consensusInstance;
        _quorum = quorum;
    }
    
    @Override
    public String getApplicationMessageType() {
        return DECIDE_BLOCK_MESSAGE;
    }

    public int getConsensusInstance() {
        return _consensusInstance;
    }

    public List<ConsensusInstanceMessage> getQuorum() {
        return _quorum;
    }

    @Override
    public String toString(int level) {
        return toStringWithTabs("DecideClientMessage: {", level) +
                toStringWithTabs("Quorum: " + _quorum.toString(), level+1) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }

}