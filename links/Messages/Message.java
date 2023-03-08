package pt.tecnico.blockchain;

public abstract class Message {
    
    private String _message;
    private int _senderPID;

    public Message(String message, int sender) {
        _message = message;
        _senderPID = sender;
    }

    public String getMessage() {
        return _message;
    }
    public int getSender() {
        return _senderPID;
    }
}
