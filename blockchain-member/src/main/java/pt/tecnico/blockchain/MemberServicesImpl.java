package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.blockchain.AppendBlockMessage;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.ibft.DecideBlockClientMessage;
import pt.tecnico.blockchain.Messages.ibft.DecideBlockMessage;

import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;

public class MemberServicesImpl {

    public static void handleRequest(ApplicationMessage message, MemberState memberState) throws IOException, NoSuchAlgorithmException {
        switch (message.getApplicationMessageType()) {
            case ApplicationMessage.APPEND_BLOCK_MESSAGE:
                AppendBlockMessage msg = (AppendBlockMessage) message;
                memberState.startIbft((BlockchainMessage) msg.getContent());
                if(memberState.checkLeader()) {
                    System.out.println("Doing PrePrePare" + "\n");
                    doPrePrepare(memberState,(BlockchainMessage) msg.getContent());

                }
                    doPrePrepare(memberState,(BlockchainMessage) msg.getContent());
                break;
            case ApplicationMessage.CONSENSUS_INSTANCE_MESSAGE:
                handleConsensusInstanceMessage((ConsensusInstanceMessage) message, memberState);
                break;
            case ApplicationMessage.DECIDE_BLOCK_MESSAGE :
                handleDecideBlockMessage((DecideBlockMessage) message, memberState);
                break;
            default:
                System.out.println("ERROR: Could not handle request");
                break;
        }
    }

    public static void handleConsensusInstanceMessage(ConsensusInstanceMessage message, MemberState memberState) throws IOException, NoSuchAlgorithmException {
        switch (message.getMessageType()) {
            case ConsensusInstanceMessage.PRE_PREPARE:
                System.out.println("Received PrePrePare" + "\n");
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

    public static void handlePrePrepareRequest(ConsensusInstanceMessage message, MemberState memberState) throws IOException, NoSuchAlgorithmException {
        System.out.println("Handling PrePrePare" + "\n");
        System.out.println(message.getContent().toString(1));
        System.out.println(message.getMessageType());

        //memberState.startTimer();
        message.setMessageType(ConsensusInstanceMessage.PREPARE);
        System.out.println(message.getMessageType());
        MemberFrontend.broadcastMessage(message);
    }

    public static void handlePrepareRequest(ConsensusInstanceMessage message, MemberState memberState) throws IOException, NoSuchAlgorithmException {
        System.out.println("Received Prepare" + "\n");
        memberState.addToPreparedQuorum(message);
        if (memberState.hasPreparedQuorum()) {
            System.out.println("Received Quorum Prepare" + "\n");
            memberState.setIbftPreparedRound(message.getRound());
            memberState.setIbftPreparedValue((BlockchainMessage) message.getContent());
            message.setMessageType(ConsensusInstanceMessage.COMMIT);
            MemberFrontend.broadcastMessage(message);
        }
    }

    public static void handleCommitRequest(ConsensusInstanceMessage message, MemberState memberState) throws IOException, NoSuchAlgorithmException {
        System.out.println("Received Prepare" + "\n");
        memberState.addToCommitQuorum(message);
        if (memberState.hasCommitQuorum()) {
            System.out.println("Received Quorum Commit" + "\n");
            //memberState.stopTimer();
            DecideBlockMessage newBlock = new DecideBlockMessage(message.getRound(), memberState.getCommitQuorum(),message.getContent(),memberState.getLastBlockHash());
            MemberFrontend.broadcastMessage(newBlock);
        }
    }

    public static void handleDecideBlockMessage(DecideBlockMessage  decideMessage, MemberState memberState) throws IOException, NoSuchAlgorithmException {
        //TO DO verify sigantures
        System.out.println("Decided Block" + decideMessage.getContent().toString() +"\n");
        BlockchainMessage blockMessage = (BlockchainMessage) decideMessage.getContent();
        String message = blockMessage.getMessage();
        if(memberState.verifyNewBlock(message,decideMessage.getHash())){
            memberState.addNewBlock(blockMessage);
            MemberFrontend.broadcastClients(new DecideBlockClientMessage(decideMessage.getQuorum(),blockMessage.getMessage()));
        }
    }

    public static void doPrePrepare(MemberState memberState,BlockchainMessage message) throws IOException, NoSuchAlgorithmException {
        ConsensusInstanceMessage prepareMessage =
                new ConsensusInstanceMessage(memberState.getConsensusInstance()
                        ,memberState.getRound(),memberState.getPid(),message);
        prepareMessage.setMessageType(ConsensusInstanceMessage.PRE_PREPARE);
        MemberFrontend.broadcastMessage(prepareMessage);
    }


}


