package pt.tecnico.blockchain.behavior.correct;

import pt.tecnico.blockchain.Ibft;
import pt.tecnico.blockchain.IbftTimer;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;

import pt.tecnico.blockchain.Keys.RSAKeyStoreById;

import static pt.tecnico.blockchain.IbftMessagehandler.broadcastMessage;

public class DefaultIbftBehavior {

    public static void handlePrePrepareRequest(ConsensusInstanceMessage message) {
        System.out.println("Received Pre Prepare");
        if (Ibft.leader(message.getConsensusInstance(), message.getRound()) == message.getSenderPID()) {
            IbftTimer.start(message.getRound());
            message.setMessageType(ConsensusInstanceMessage.PREPARE);
            message.signMessage(RSAKeyStoreById.getPrivateKey(Ibft.getPid()), message);
            broadcastMessage(message);
        }
        System.out.println("Received Pre Prepare from a fake leader with PID: " + message.getSenderPID());
    }

    public static void handlePrepareRequest(ConsensusInstanceMessage message) {
        System.out.println("Received Prepare");
        Ibft.addToPreparedQuorum(message);
        if (Ibft.hasValidPreparedQuorum()) {
            System.out.println("Received Quorum Prepare" + "\n");
            Ibft.setPreparedRound(message.getRound());
            Ibft.setPreparedValue(message.getContent());
            message.setMessageType(ConsensusInstanceMessage.COMMIT);
            message.signMessage(RSAKeyStoreById.getPrivateKey(Ibft.getPid()), message);
            broadcastMessage(message);
        }
    }

    public static void handleCommitRequest(ConsensusInstanceMessage message) {
        System.out.println("Received Prepare");
        Ibft.addToCommitQuorum(message);
        Content value = message.getContent();
        if (Ibft.hasValidCommitQuorum()) {
            System.out.println("Received Quorum Commit" + "\n");
            IbftTimer.stop();
            if (Ibft.getApp().validateValue(value)){
                Ibft.getApp().decide(new DecideBlockMessage(
                    Ibft.getConsensusInstance(), message.getContent(), Ibft.getCommitQuorum()
                ));
                Ibft.endInstance();
            }
        }
    }
}
