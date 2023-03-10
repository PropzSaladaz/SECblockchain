package pt.tecnico.blockchain.Messages;

import java.net.InetAddress;

public abstract class Message {
    
    private String _message;
    private int _senderPID;
    private InetAddress _senderHostname;
    private int _senderPort;

    public Message(String message, int senderPID, InetAddress senderHostname, int senderPort) {
        _message = message;
        _senderPID = senderPID;
        _senderHostname = senderHostname;
        _senderPort = senderPort;
    }

    public String getMessage() {
        return _message;
    }
    public int getSenderPID() {
        return _senderPID;
    }
    public InetAddress getSenderHostname() {
        return _senderHostname;
    }

    public int getSenderPort() {
        return _senderPort;
    }
}
