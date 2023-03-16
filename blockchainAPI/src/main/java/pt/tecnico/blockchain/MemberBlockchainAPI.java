package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;

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
    public void decide(Content value) {
        chain.decide(value);
        broadcastClients(value);
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

    public void broadcastClients(Content message) {
        try {
            for (Pair<String, Integer> pair : _clientHostNames ){
                AuthenticatedPerfectLink.send(_socket, message, pair.getFirst(), pair.getSecond());
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
