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
        StringBuilder quorums = new StringBuilder();
        for (ConsensusInstanceMessage m : _quorum) {
            quorums.append(m.toString(level+2));
        }
        return toStringWithTabs("DecideBlockMessage: {", level) +
                toStringWithTabs("Quorum:\n" + quorums ,level+1) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }

    @Override
    public boolean equals(Content another) {
        try {
            DecideBlockMessage m = (DecideBlockMessage) another;
            if (_quorum.size() == m.getQuorum().size()) {
                for (int i = 0 ; i < _quorum.size() ; i++) {
                    if (!_quorum.get(i).equals(m.getQuorum().get(i))) return false;
                }
                return _consensusInstance == m.getConsensusInstance();
            }
            return false;
        } catch( ClassCastException e) {
            return false;
        }

    }

}