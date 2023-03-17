package pt.tecnico.blockchain.behavior.corrupt;

import pt.tecnico.blockchain.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Ibft;
import pt.tecnico.blockchain.IbftTimer;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Pair;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static pt.tecnico.blockchain.IbftMessagehandler.broadcastMessage;

public class CorruptIbftBehavior {

    public static void handlePrePrepareRequest(ConsensusInstanceMessage message) {
        message.setMessageType(ConsensusInstanceMessage.PREPARE);
        broadcastMessage(message);
    }

    public static void handlePrepareRequest(ConsensusInstanceMessage message) {
        BlockchainMessage corrupted = new BlockchainMessage("Invalid value to append");
        message.setContent(corrupted);
        message.setMessageType(ConsensusInstanceMessage.COMMIT);
        broadcastMessage(message);
    }

    public static void handleCommitRequest(ConsensusInstanceMessage message) {
        BlockchainMessage corrupted = new BlockchainMessage("Invalid value to append");
        Ibft.getApp().decide(corrupted, Ibft.getCommitQuorum());
        Ibft.endInstance();

    }
}
