package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.blockchain.AppendBlockMessage;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.ibft.DecideBlockClientMessage;
import pt.tecnico.blockchain.Messages.ibft.DecideBlockMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ClientServiceImpl {

    public static void handleRequest(ApplicationMessage message)  {
        switch (message.getApplicationMessageType()) {
            case ApplicationMessage.DECIDE_BLOCK_CLIENT :
                DecideBlockClientMessage decidedBlock = (DecideBlockClientMessage) message;
                System.out.println("APPENDED: " + decidedBlock.getContent().toString());
                break;
            default:
                System.out.println("ERROR: Could not handle request");
                break;
        }
    }
}
