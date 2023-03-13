package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.*;

public class MemberServicesImpl {

    public static void handleRequest(Message message, MemberState memberState) {
        Content msgContent = message.getContent();
        switch (msgContent.getContentType()) {
            case ContentType.APPEND_BLOCK:
                ConsensusInstanceMessage msg = (ConsensusInstanceMessage) msgContent;
                memberState.startIbft(msg.getConsensusInstance(), msg.getValue());
                break;
            case ContentType.CONSENSUS_INSTANCE:
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
            DecideBlockMessage newBlock = new DecideBlockMessage(
                message.getRound(), message.getValue(), memberState.getCommitQuorum());
            // DECIDE(newBlock) -> Call to an external entity
        }
    }
}