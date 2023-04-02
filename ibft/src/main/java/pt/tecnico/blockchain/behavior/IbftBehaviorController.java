package pt.tecnico.blockchain.behavior;

import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.behavior.correct.IbftCorrect;
import pt.tecnico.blockchain.behavior.correct.IbftBehavior;

import java.security.NoSuchAlgorithmException;

public class IbftBehaviorController {
    private static IbftBehavior behavior = new IbftCorrect();

    public static void changeState(IbftBehavior state) {
        behavior = state;
//        System.out.println("IBFT BEHAVIOR: changed state to " + behavior.TYPE());
    }

    public static String getBehaviorType() {
        return behavior.TYPE();
    }

    public static void handlePrePrepareRequest(ConsensusInstanceMessage message) {
        behavior.handlePrePrepareRequest(message);
    }

    public static void handlePrepareRequest(ConsensusInstanceMessage message) {
        behavior.handlePrepareRequest(message);
    }

    public static void handleCommitRequest(ConsensusInstanceMessage message){
        behavior.handleCommitRequest(message);
    }
}
