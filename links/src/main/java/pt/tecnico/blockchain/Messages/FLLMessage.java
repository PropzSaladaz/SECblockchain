package pt.tecnico.blockchain.Messages;

import java.net.InetAddress;

public class FLLMessage extends Message implements Content{


    public FLLMessage() {
    }

    public FLLMessage(Content content) {
        super(content);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int level) {
        return _content.toString(level);
    }

}
