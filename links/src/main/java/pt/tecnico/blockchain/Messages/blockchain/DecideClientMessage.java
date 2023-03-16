package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import java.util.List;

public class DecideClientMessage extends ApplicationMessage implements Content {

    private List<Integer> _quorum;
    private String _message;

    public DecideClientMessage(List<Integer> quorum, String message){
        _quorum = quorum;
        _message = message;
    }

    @Override
    public String getApplicationMessageType() {
        return super.DECIDE_BLOCK_CLIENT;
    }


    public List<Integer> getQuorum() {
        return _quorum;
    }

    @Override
    public String toString(int level) {
        return toStringWithTabs("DecideClientMessage: {", level) +
                toStringWithTabs("message: " + _message, level+1) +
                toStringWithTabs("Quorum: " + _quorum.toString(), level+1) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }

}