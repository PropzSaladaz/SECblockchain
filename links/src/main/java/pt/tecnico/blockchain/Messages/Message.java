package pt.tecnico.blockchain.Messages;

import java.net.InetAddress;

public abstract class Message {

    private Content _content;

    public Message(Content content){
        _content = content;
    }

    public Content getContent() {
        return _content;
    }

    @Override
     public String toString(){
        return _content.toString();
    }
}

