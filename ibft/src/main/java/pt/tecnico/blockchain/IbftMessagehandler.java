package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

class IbftMessagehandler {
    static ArrayList<Pair<String, Integer>> _memberHostNames;
    static DatagramSocket _socket;


    public static void handleMessage(ConsensusInstanceMessage message) {
        switch (message.getMessageType()) {
            case ConsensusInstanceMessage.PRE_PREPARE:
                System.out.println("Received PrePrePare" + "\n");
                handlePrePrepareRequest(message);
                break;
            case ConsensusInstanceMessage.PREPARE:
                handlePrepareRequest(message);
                break;
            case ConsensusInstanceMessage.COMMIT:
                handleCommitRequest(message);
                break;
            default:
                System.out.println("ERROR: Could not handle request");
                break;
        }
    }

    public static void handlePrePrepareRequest(ConsensusInstanceMessage message) {
        // TODO check if it is valid and is from the leader
//        System.out.println("Handling PrePrePare" + "\n");
//        System.out.println(message.getContent().toString(1));
//        System.out.println(message.getMessageType());

        //memberState.startTimer();
        message.setMessageType(ConsensusInstanceMessage.PREPARE);
//        System.out.println(message.getMessageType());
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
            //memberState.stopTimer();
            // add to blockchain
            if(Ibft.getApp().validateValue(value)){
                Ibft.getApp().decide(value, Ibft.getCommitQuorum());
                Ibft.endInstance();
            }
        }
    }

    public static void doPrePrepare(Content message) {
        ConsensusInstanceMessage prepareMessage =
                new ConsensusInstanceMessage(Ibft.getConsensusInstance()
                        ,Ibft.getRound(), Ibft.getPid(), message);
        prepareMessage.setMessageType(ConsensusInstanceMessage.PRE_PREPARE);
        broadcastMessage(prepareMessage);
    }

    public static void broadcastMessage(Content message) {
        try {
            for (Pair<String, Integer> pair : _memberHostNames ){
                AuthenticatedPerfectLink.send(_socket, message, pair.getFirst(), pair.getSecond());
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void init(DatagramSocket socket, ArrayList<Pair<String, Integer>> memberHostNames) {
        _socket = socket;
        _memberHostNames = memberHostNames;
    }
}
