package pt.tecnico.blockchain.behavior.correct;

import pt.tecnico.blockchain.Ibft;
import pt.tecnico.blockchain.IbftTimer;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;

import pt.tecnico.blockchain.Keys.RSAKeyStoreById;

import static pt.tecnico.blockchain.IbftMessagehandler.broadcastMessage;

public class DefaultIbftBehavior {

    public synchronized static void handlePrePrepareRequest(ConsensusInstanceMessage message) {
//        System.out.println("Received Pre Prepare");
        if (Ibft.leader(message.getConsensusInstance(), message.getRound()) == message.getSenderPID() &&
                message.getConsensusInstance() == Ibft.getConsensusInstance()) {
            ConsensusInstanceMessage msg = new ConsensusInstanceMessage(message.getConsensusInstance(),
            message.getRound(), Ibft.getPid(), message.getContent());
            msg.setMessageType(ConsensusInstanceMessage.PREPARE);
            msg.signMessage(RSAKeyStoreById.getPrivateKey(Ibft.getPid()), message.getContent());
            broadcastMessage(msg);
        }
        IbftTimer.start(message.getRound());
//        System.out.println("Received Pre Prepare from a fake leader with PID: " + message.getSenderPID());
    }

    public synchronized static void handlePrepareRequest(ConsensusInstanceMessage message) {
//        System.out.println("Received Prepare");
        Ibft.addToPreparedQuorum(message);
        if (Ibft.hasValidPreparedQuorum()) {
//            System.out.println("Received Quorum Prepare" + "\n");
            Ibft.setPreparedRound(message.getRound());
            Ibft.setPreparedValue(message.getContent());
            ConsensusInstanceMessage msg = new ConsensusInstanceMessage(message.getConsensusInstance(),
                    message.getRound(), Ibft.getPid(), message.getContent());
            msg.setMessageType(ConsensusInstanceMessage.COMMIT);
            msg.signMessage(RSAKeyStoreById.getPrivateKey(Ibft.getPid()), message.getContent());
            broadcastMessage(msg);
        }
    }

    public synchronized static void handleCommitRequest(ConsensusInstanceMessage message) {
//        System.out.println("Received Commit");
        if (Ibft.hasSamePreparedValue(message)) {
            Ibft.addToCommitQuorum(message);
            if (Ibft.hasValidCommitQuorum()) {
                Content value = message.getContent();
                IbftTimer.stop();
                if (Ibft.getApp().validateValue(value)) {
                    System.out.println("Committed quorum ->   " + Ibft.getCommitQuorum());
                    Ibft.getApp().decide(new DecideBlockMessage(
                            Ibft.getConsensusInstance(), message.getContent(), Ibft.getCommitQuorum()
                    ));
                    Ibft.endInstance();
                    System.out.println("Quorum size after endisntance = " + Ibft.getCommitQuorum().size());
                }
            }
        }
    }
}
