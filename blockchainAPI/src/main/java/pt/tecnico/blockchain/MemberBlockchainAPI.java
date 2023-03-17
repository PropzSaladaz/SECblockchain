package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;

public class MemberBlockchainAPI implements Application {
    private Blockchain chain;
    private DatagramSocket _socket;

    public MemberBlockchainAPI(DatagramSocket socket) {
        chain = new Blockchain();
        _socket = socket;
    }

    @Override
    public void decide(Content message) {
        DecideBlockMessage decideMsg = (DecideBlockMessage) message;
        chain.decide(decideMsg.getContent());
        broadcastToClient(decideMsg);
    }

    @Override
    public boolean validateValue(Content value) {
        return chain.validateValue(value);
    }

    @Override
    public int getNextInstanceNumber() {
        return chain.getNextInstanceNumber();
    }

    @Override
    public void prepareValue(Content value) {
        chain.prepareValue(value);
    }

    public void broadcastToClient(Content message) {
        try {
//            System.out.println("Broadcasting decide block message to client");
            DecideBlockMessage decideMsg = (DecideBlockMessage) message;
            BlockchainMessage blockMessage = (BlockchainMessage) decideMsg.getContent();
            AuthenticatedPerfectLink.send(_socket, message, blockMessage.getAddress(), blockMessage.getPort());

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}