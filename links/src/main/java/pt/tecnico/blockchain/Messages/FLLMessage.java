package pt.tecnico.blockchain.Messages;

import java.net.InetAddress;

public class FLLMessage extends Message {
    
    public FLLMessage(String message, int sender, InetAddress senderHostname, int senderPort) {
        super(message, sender, senderHostname, senderPort);
    }
}
