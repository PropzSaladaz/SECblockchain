package pt.tecnico.blockchain.behavior.corrupt;

import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.behavior.correct.IbftBehavior;

public class IbftCorrupt extends IbftBehavior {
    @Override
    public String TYPE() {
        return "Ibft corrupt";
    }

    @Override
    public void handlePrePrepareRequest(ConsensusInstanceMessage message) {
        CorruptIbftBehavior.handlePrePrepareRequest(message);
    }

    @Override
    public void handlePrepareRequest(ConsensusInstanceMessage message) {
        CorruptIbftBehavior.handlePrepareRequest(message);
    }

    @Override
    public void handleCommitRequest(ConsensusInstanceMessage message) {
        CorruptIbftBehavior.handleCommitRequest(message);
    }
}
