package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

public class AppendBlockMessage extends ApplicationMessage implements Content {

    public AppendBlockMessage(Content content) {
        super(content);
    }

    @Override
    public String getApplicationMessageType() {
        return super.APPEND_BLOCK_MESSAGE;
    }

    @Override
    public String toString(int level) {
        return toStringWithTabs("AppendBlockMessage: {", level) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }
}
