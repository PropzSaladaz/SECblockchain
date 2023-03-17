package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MemberBlockchainAPI implements Application {
    private Blockchain chain;
    private ArrayList<Pair<String, Integer>> _clientHostNames;
    private DatagramSocket _socket;

    public MemberBlockchainAPI(DatagramSocket socket, ArrayList<Pair<String, Integer>> clients) {
        chain = new Blockchain();
        _socket = socket;
        _clientHostNames = clients;
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
            DecideBlockMessage decideMsg = (DecideBlockMessage) message;
            BlockchainMessage blockMessage = (BlockchainMessage) decideMsg.getContent();
            AuthenticatedPerfectLink.send(_socket, decideMsg, blockMessage.getAddress(), blockMessage.getPort());

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}