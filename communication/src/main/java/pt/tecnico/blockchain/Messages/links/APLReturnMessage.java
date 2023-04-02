package pt.tecnico.blockchain.Messages.links;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.Message;

public class APLReturnMessage extends Message implements Content {

    private int senderPid;

    public APLReturnMessage(Content content, int senderPid) {
        super(content);
        this.senderPid = senderPid;
    }

    public int getSenderPid() {
        return senderPid;
    }

    @Override
    public String toString(int tabs) {
        return null;
    }

    @Override
    public boolean equals(Content another) {
        return false;
    }
}
