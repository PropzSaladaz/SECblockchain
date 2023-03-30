package pt.tecnico.blockchain.Messages.links;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.Message;

public class FLLMessage extends Message implements Content {


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
        return getContent().toString(level);
    }

    @Override
    public boolean equals(Content another) {
        try {
            FLLMessage m = (FLLMessage) another;
            return getContent().equals(m.getContent());
        } catch(ClassCastException e) {
            return false;
        }
    }

}
