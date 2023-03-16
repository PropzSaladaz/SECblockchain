package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Blockchain;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.blockchain.DecideClientMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
    public void decide(Content value, List<Integer> commitQuorum) {
        chain.decide(value, commitQuorum);
        broadcastClients(value, commitQuorum);
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

    public void broadcastClients(Content message, List<Integer> commitQuorum) {
        try {
            for (Pair<String, Integer> pair : _clientHostNames ){
                DecideClientMessage msg = new DecideClientMessage(commitQuorum, message);
                AuthenticatedPerfectLink.send(_socket, msg, pair.getFirst(), pair.getSecond());
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
