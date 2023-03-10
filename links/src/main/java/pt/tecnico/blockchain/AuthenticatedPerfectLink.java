package pt.tecnico.blockchain;

import java.io.IOException;

import java.net.DatagramSocket;
import java.security.PublicKey;
import java.security.PrivateKey;

import pt.tecnico.blockchain.Messages.APLMessage;

public class AuthenticatedPerfectLink {

    private PublicKey _myPublicKey;
    private PrivateKey _myPrivateKey;
    private PublicKey _hostPublicKey;
    private PerfectLink _perfectLink = new PerfectLink();

    public AuthenticatedPerfectLink(PublicKey myPubKey, PrivateKey myPrivKey, PublicKey hostPubKey) {
        _myPublicKey = myPubKey;
        _myPrivateKey = myPrivKey;
        _hostPublicKey = hostPubKey;
    }
    
    public void send(DatagramSocket socket, APLMessage message) throws IOException {
        // TODO
    }

    public APLMessage deliver() throws IOException {
        // TODO
        return null;
    }
}
