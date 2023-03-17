package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import java.util.stream.Collectors;


class IbftMessagehandler {
    static ArrayList<Pair<String, Integer>> _memberHostNames;
    static DatagramSocket _socket;
    static int _pid;

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
        System.out.println("Received Pre Prepare");
        if (Ibft.leader(message.getConsensusInstance(), message.getRound()) == message.getSenderPID()) {
            IbftTimer.start(message.getRound());
            message.setMessageType(ConsensusInstanceMessage.PREPARE);
            message.signMessage(RSAKeyStoreById.getPrivateKey(_pid), message);
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
            message.signMessage(RSAKeyStoreById.getPrivateKey(_pid), message);
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
    
    public static void broadcastPrePrepare(Content message) {
        ConsensusInstanceMessage prepareMessage =
                new ConsensusInstanceMessage(Ibft.getConsensusInstance()
                        ,Ibft.getRound(), Ibft.getPid(), message);
        prepareMessage.setMessageType(ConsensusInstanceMessage.PRE_PREPARE);
        broadcastMessage(prepareMessage);
    }

    public static void broadcastMessage(Content message) {
        try {
            System.out.println(_memberHostNames.size());
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
