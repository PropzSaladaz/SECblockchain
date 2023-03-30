package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.Application;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;

public class BlockchainMemberAPI implements Application {
    private Blockchain chain;
    private SynchronizedTransactionPool pool;
    private DatagramSocket _socket;

    public BlockchainMemberAPI(DatagramSocket socket) {
        chain = new Blockchain();
        _socket = socket;
        pool = new SynchronizedTransactionPool();
    }

    @Override
    public void decide(Content message) {
        DecideBlockMessage decideMsg = (DecideBlockMessage) message;
        chain.decide(decideMsg.getContent());
        sendDecisionToClient(decideMsg);
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

    public void sendDecisionToClient(Content message) {
        try {
//            System.out.println("Sending decide block message to client");
            DecideBlockMessage decideMsg = (DecideBlockMessage) message;
            BlockchainBlock blockMessage = (BlockchainBlock) decideMsg.getContent();
            AuthenticatedPerfectLink.send(_socket, message, blockMessage.getAddress(), blockMessage.getPort());

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}