package pt.tecnico.blockchain.Messages;

public class DecideBlockMessage extends Message implements Content {

    private int _round;
    private BlockchainMessage _value;
    private int[] _quorum;

    public DecideBlockMessage(int round, BlockchainMessage value, int[] quorum) {
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

    public int[] getQuorum() {
        return _quorum;
    }

    public BlockchainMessage getValue() {
        return _value;
    } 
}
