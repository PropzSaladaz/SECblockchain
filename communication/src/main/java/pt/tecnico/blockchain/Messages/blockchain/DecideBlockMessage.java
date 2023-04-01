package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

import java.util.List;

public class DecideBlockMessage extends ApplicationMessage implements Content {
    public static final String SUCCESSFUL_TRANSACTION = "SUCCESSFUL TRANSACTION";
    public static final String REJECTED_TRANSACTION = "REJECTED TRANSACTION";

    private String _status;

    public DecideBlockMessage(){}

    public void setContent(Content transaction){ setContent(transaction);}
    
    @Override
    public String getApplicationMessageType() {return DECIDE_BLOCK_MESSAGE;}


    public void setStatus(String status) {_status = status;}

    public String getStatus() {return _status;}

    @Override
    public String toString(int level) {
        return toStringWithTabs("DecideBlockMessage: {", level) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }

    @Override
    public boolean equals(Content another) {
        try {
            DecideBlockMessage m = (DecideBlockMessage) another;
            return this.getContent() == m.getContent();
        } catch( ClassCastException e) {
            return false;
        }

    }

}