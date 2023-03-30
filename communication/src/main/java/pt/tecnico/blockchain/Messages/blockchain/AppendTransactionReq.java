package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

public class AppendTransactionReq extends ApplicationMessage implements Content {


    public AppendTransactionReq(Content content) {
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
            AppendTransactionReq m = (AppendTransactionReq) another;
            return getContent().equals(m.getContent());
        } catch (ClassCastException c) {
            return false;
        }
    }

}
