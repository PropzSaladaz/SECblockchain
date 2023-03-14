package pt.tecnico.blockchain.Messages.ibft;

import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.Message;

public class ConsensusInstanceMessage extends Message implements Content {

    public static final String PRE_PREPARE = "PRE-PREPARE";
    public static final String PREPARE = "PREPARE";
    public static final String COMMIT = "COMMIT";

    private String _messageType;
    private int _consensusInstance;
    private int _roundNumber;
    private int _senderPID;
    private BlockchainMessage _value;

    public ConsensusInstanceMessage(String messageType, int consensusInstance, int roundNumber,
                                    BlockchainMessage value, int senderPID) {
        _messageType = messageType;
        _consensusInstance = consensusInstance;
        _roundNumber = roundNumber;
        _value = value;
        _senderPID = senderPID;
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

    public int getSenderPID() {
        return _senderPID;
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
