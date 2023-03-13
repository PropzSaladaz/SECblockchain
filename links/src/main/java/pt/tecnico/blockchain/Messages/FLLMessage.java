package pt.tecnico.blockchain.Messages;

public class FLLMessage extends Message implements Content{


    public FLLMessage() {
    }

    public FLLMessage(Content content, int senderPID) {
        super(content, senderPID);
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
