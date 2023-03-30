package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

public class AppendBlockMessage extends ApplicationMessage implements Content {


    public AppendBlockMessage(Content content) {
        super(content);
    }

    @Override
    public String getApplicationMessageType() {
        return APPEND_BLOCK_MESSAGE;
    }

    @Override
    public String toString(int level) {
        return toStringWithTabs("AppendBlockMessage: {", level) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }

    @Override
    public boolean equals(Content another) {
        try {
            AppendBlockMessage m = (AppendBlockMessage) another;
            return getContent().equals(m.getContent());
        } catch (ClassCastException c) {
            return false;
        }
    }

}