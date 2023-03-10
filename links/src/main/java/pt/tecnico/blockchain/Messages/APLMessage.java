package pt.tecnico.blockchain.Messages;

import java.net.InetAddress;

public class APLMessage extends PLMessage {
    
    private byte[] _signature;

    public APLMessage(String message, int sender, InetAddress senderHostname, int senderPort) {
        super(message, sender, senderHostname, senderPort);
    }

    public APLMessage(String message, int sender, InetAddress senderHostname, int senderPort, byte[] signature) {
        super(message, sender, senderHostname, senderPort);
        _signature = signature;
    }

    public byte[] getSignatureBytes() {
        return _signature;
    }

    public void setSignature(byte[] signature) {
        _signature = signature;
    }
}
