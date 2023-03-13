package pt.tecnico.blockchain.Messages;

import java.io.Serializable;

public abstract class Message implements Serializable {

    Content _content;
    private int _senderPID;

    public Message(){
    }

    public Message(Content content, int senderPID){
        _content = content;
        _senderPID = senderPID;
    }

    public Content getContent() {
        return _content;
    }

    public int getSenderPID() {
        return _senderPID;
    }

    public void setSenderPID(int value) {
        _senderPID = value;
    }

    @Override
     public String toString(){
        return _content.toString();
    }

}

