package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import java.util.List;

public class DecideClientMessage extends ApplicationMessage implements Content {

    private List<Integer> _quorum;

    public DecideClientMessage(List<Integer> quorum, Content message){
        super(message);
        _quorum = quorum;
    }

    @Override
    public String getApplicationMessageType() {
        return DECIDE_BLOCK_CLIENT;
    }


    public List<Integer> getQuorum() {
        return _quorum;
    }

    @Override
    public String toString(int level) {
        return toStringWithTabs("DecideClientMessage: {", level) +
                toStringWithTabs("Quorum: " + _quorum.toString(), level+1) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }

}