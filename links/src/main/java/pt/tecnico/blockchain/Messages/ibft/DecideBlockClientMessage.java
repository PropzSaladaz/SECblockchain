package pt.tecnico.blockchain.Messages.ibft;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import java.util.List;

public class DecideBlockClientMessage extends ApplicationMessage implements Content {

    private List<Integer> _quorum;
    private String _message;

    public DecideBlockClientMessage(List<Integer> quorum, String message){
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
    public String toString (){
        return "TODO TODO: EMPTY METHOD";
    }

    @Override
    public String toString(int level) {
        return "TODO TODO: EMPTY METHOD";
    }

}