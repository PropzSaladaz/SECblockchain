package pt.tecnico.blockchain.Messages;

import java.io.Serializable;

public abstract class Message implements Serializable {

    private Content _content;

    public Message(){
    }

    public Message(Content content){
        _content = content;
    }

    public Content getContent() {
        return _content;
    }

    public void setContent(Content content) {
        _content = content;
    }

    @Override
     public String toString(){
        return _content.toString();
    }

}

