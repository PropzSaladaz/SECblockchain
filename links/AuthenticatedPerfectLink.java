package pt.tecnico.blockchain;

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
        
    }

    public APLMessage deliver() {

    }
}
