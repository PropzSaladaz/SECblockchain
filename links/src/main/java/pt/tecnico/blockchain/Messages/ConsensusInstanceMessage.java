package pt.tecnico.blockchain.Messages;

public class ConsensusInstanceMessage extends Message implements Content {

    public static final String PRE_PREPARE = "PRE-PREPARE";
    public static final String PREPARE = "PREPARE";
    public static final String COMMIT = "COMMIT";

    private String _messageType;
    private int _consensusInstance;
    private int _roundNumber;
    private BlockchainMessage _value;

    public ConsensusInstanceMessage(String messageType, int consensusInstance, int roundNumber, BlockchainMessage value) {
        _messageType = messageType;
        _consensusInstance = consensusInstance;
        _roundNumber = roundNumber;
        _value = value;
    }

    @Override
    public String getContentType() {
        return ContentType.CONSENSUS_INSTANCE;
    }

    public String getMessageType() {
        return _messageType;
    }

    public void setMessageType(String newType) {
        _messageType = newType;
    }

    public int getConsensusInstance() {
        return _consensusInstance;
    }

    public int getRound() {
        return _roundNumber;
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
