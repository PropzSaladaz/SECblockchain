package pt.tecnico.blockchain.Messages;

import java.io.Serializable;
import java.net.InetAddress;

public abstract class Message implements Serializable {

    Content _content;

    public Message(){
    }

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

