package pt.tecnico.blockchain.behavior.correct;

import pt.tecnico.blockchain.Ibft;
import pt.tecnico.blockchain.IbftMessagehandler;
import pt.tecnico.blockchain.IbftTimer;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

import static pt.tecnico.blockchain.IbftMessagehandler.broadcastMessage;

public class DefaultIbftBehavior {
    public static void handlePrePrepareRequest(ConsensusInstanceMessage message) {
        // TODO check if it is valid and is from the leader
        //memberState.startTimer();
        message.setMessageType(ConsensusInstanceMessage.PREPARE);
        broadcastMessage(message);
    }

    public static void handlePrepareRequest(ConsensusInstanceMessage message) {
        // TODO check if it is valid
//        System.out.println("Received Prepare" + "\n");
        Ibft.addToPreparedQuorum(message);
        if (Ibft.hasPreparedQuorum()) {
            System.out.println("Received Quorum Prepare" + "\n");
            Ibft.setPreparedRound(message.getRound());
            Ibft.setPreparedValue(message.getContent());
            message.setMessageType(ConsensusInstanceMessage.COMMIT);
            broadcastMessage(message);
        }
    }

    public static void handleCommitRequest(ConsensusInstanceMessage message) {
        // TODO check if it is valid
//        System.out.println("Received Prepare" + "\n");
        Ibft.addToCommitQuorum(message);
        Content value = message.getContent();
        if (Ibft.hasCommitQuorum()) {
            System.out.println("Received Quorum Commit" + "\n");
            IbftTimer.stop();
            // add to blockchain
            if (Ibft.getApp().validateValue(value)){
                Ibft.getApp().decide(value, Ibft.getCommitQuorum());
                Ibft.endInstance();
            }
        }
    }
}
