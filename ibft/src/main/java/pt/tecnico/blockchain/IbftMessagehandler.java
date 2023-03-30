package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.behavior.IbftBehaviorController;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class IbftMessagehandler {
    static ArrayList<Pair<String, Integer>> _memberHostNames;
    static DatagramSocket _socket;
    static int _pid;

    public static void handleMessage(ConsensusInstanceMessage message) {
        switch (message.getMessageType()) {
            case ConsensusInstanceMessage.PRE_PREPARE:
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

    public static synchronized void handlePrePrepareRequest(ConsensusInstanceMessage message) {
        IbftBehaviorController.handlePrePrepareRequest(message);
    }
    
    public static synchronized void handlePrepareRequest(ConsensusInstanceMessage message) {
        IbftBehaviorController.handlePrepareRequest(message);
    }

    public static synchronized void handleCommitRequest(ConsensusInstanceMessage message) {
        IbftBehaviorController.handleCommitRequest(message);
    }
    
    public static void broadcastPrePrepare(Content message) {
        ConsensusInstanceMessage prepareMessage =
                new ConsensusInstanceMessage(Ibft.getConsensusInstance()
                        ,Ibft.getRound(), Ibft.getPid(), message);
        prepareMessage.setMessageType(ConsensusInstanceMessage.PRE_PREPARE);
        broadcastMessage(prepareMessage);
    }

    public static void broadcastMessage(Content message) {
        try {
//            System.out.println(_memberHostNames.size());
            for (Pair<String, Integer> pair : _memberHostNames ){
                AuthenticatedPerfectLink.send(_socket, message, pair.getFirst(), pair.getSecond());
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void init(DatagramSocket socket, ArrayList<Pair<String, Integer>> memberHostNames, int pid) {
        _pid = pid;
        _socket = socket;
        _memberHostNames = memberHostNames;
    }
}
