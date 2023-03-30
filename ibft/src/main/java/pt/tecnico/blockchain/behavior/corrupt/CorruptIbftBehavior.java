package pt.tecnico.blockchain.behavior.corrupt;

import pt.tecnico.blockchain.Ibft;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;


import static pt.tecnico.blockchain.IbftMessagehandler.broadcastMessage;

public class CorruptIbftBehavior {

    public static void handlePrePrepareRequest(ConsensusInstanceMessage message) {
        message.setMessageType(ConsensusInstanceMessage.PREPARE);
        broadcastMessage(message);
    }

    public static void handlePrepareRequest(ConsensusInstanceMessage message) {
        // TODO
    }

    public static void handleCommitRequest(ConsensusInstanceMessage message) {
        // TODO
    }
}
