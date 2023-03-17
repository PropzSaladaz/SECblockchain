package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;

public class ClientServiceImpl {

    public static void handleRequest(ApplicationMessage message)  {
        switch (message.getApplicationMessageType()) {
            case ApplicationMessage.DECIDE_BLOCK_MESSAGE:
                DecideBlockMessage decidedBlock = (DecideBlockMessage) message;
                System.out.println("APPENDED: " + decidedBlock.getContent().toString());
                break;
            default:
                System.out.println("ERROR: Could not handle request");
                break;
        }
    }
}
