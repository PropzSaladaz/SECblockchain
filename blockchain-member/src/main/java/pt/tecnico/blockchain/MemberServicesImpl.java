package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.blockchain.AppendBlockMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.blockchain.DecideMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MemberServicesImpl {

    public static void handleRequest(Content message) {
        try {
            ApplicationMessage appMsg = (ApplicationMessage) message;
            switch (appMsg.getApplicationMessageType()) {
                case ApplicationMessage.APPEND_BLOCK_MESSAGE:
                    AppendBlockMessage msg = (AppendBlockMessage) message;
                    Ibft.start(msg.getContent());
                    break;
                case ApplicationMessage.CONSENSUS_INSTANCE_MESSAGE:
                    ConsensusInstanceMessage ibftMessage = (ConsensusInstanceMessage) message;
                    Ibft.handleMessage(ibftMessage);
                    break;
                default:
                    System.out.println("ERROR: Could not handle request");
                    break;
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}


