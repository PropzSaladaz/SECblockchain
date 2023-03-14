package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.blockchain.AppendBlockMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.ibft.DecideBlockMessage;

public class MemberServicesImpl {

    public static void handleRequest(ApplicationMessage message, MemberState memberState) {
        switch (message.getApplicationMessageType()) {
            case ApplicationMessage.APPEND_BLOCK_MESSAGE:
                AppendBlockMessage msg = (AppendBlockMessage) message;
//                memberState.startIbft(msg.getContent());
                break;
            case ApplicationMessage.CONSENSUS_INSTANCE_MESSAGE:
                handleConsensusInstanceMessage((ConsensusInstanceMessage) message, memberState);
                break;
            default:
                System.out.println("ERROR: Could not handle request");
                break;
        }
    }

    public static void handleConsensusInstanceMessage(ConsensusInstanceMessage message, MemberState memberState) {
        switch (message.getMessageType()) {
            case ConsensusInstanceMessage.PRE_PREPARE:
                handlePrePrepareRequest(message, memberState);
                break;
            case ConsensusInstanceMessage.PREPARE:
                handlePrepareRequest(message, memberState);
                break;
            case ConsensusInstanceMessage.COMMIT:
                handleCommitRequest(message, memberState);
                break;
            default:
                System.out.println("ERROR: Could not handle request");
                break;
        }
    }

    public static void handlePrePrepareRequest(ConsensusInstanceMessage message, MemberState memberState) {
        memberState.startTimer();
        message.setMessageType(ConsensusInstanceMessage.PREPARE);
        MemberFrontend.broadcastMessage(message);
    }

    public static void handlePrepareRequest(ConsensusInstanceMessage message, MemberState memberState) {
        memberState.addToPreparedQuorum(message);
        if (memberState.hasPreparedQuorum()) {
            memberState.setIbftPreparedRound(message.getRound());
            memberState.setIbftPreparedValue(message.getValue());
            message.setMessageType(ConsensusInstanceMessage.COMMIT);
            MemberFrontend.broadcastMessage(message);
        }
    }

    public static void handleCommitRequest(ConsensusInstanceMessage message, MemberState memberState) {
        memberState.addToCommitQuorum(message);
        if (memberState.hasCommitQuorum()) {
            memberState.stopTimer();
//            DecideBlockMessage newBlock = new DecideBlockMessage(
//                message.getRound(), message.getValue(), memberState.getCommitQuorum());
            // DECIDE(newBlock) -> Call to an external entity
        }
    }
}