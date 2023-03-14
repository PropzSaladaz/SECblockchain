package pt.tecnico.blockchain.Messages.ibft;

import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.Message;

import java.util.List;

public class DecideBlockMessage extends Message implements Content {

    private int _round;
    private BlockchainMessage _value;
    private List<Integer> _quorum;

    public DecideBlockMessage(int round, BlockchainMessage value, List<Integer> quorum) {
        _round = round;
        _value = value;
        _quorum = quorum;
    }
    
    @Override
    public String getContentType() {
        return ContentType.DECIDE_BLOCK;
    }

    public int getRound() {
        return _round;
    }

    public List<Integer> getQuorum() {
        return _quorum;
    }

    public BlockchainMessage getValue() {
        return _value;
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
