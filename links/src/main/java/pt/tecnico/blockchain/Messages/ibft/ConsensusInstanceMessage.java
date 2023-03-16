package pt.tecnico.blockchain.Messages.ibft;

import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

public class ConsensusInstanceMessage extends ApplicationMessage implements Content {

    public static final String PRE_PREPARE = "PRE-PREPARE";
    public static final String PREPARE = "PREPARE";
    public static final String COMMIT = "COMMIT";

    private String _messageType;
    private int _consensusInstance;
    private int _roundNumber;
    private int _senderPID;

    public ConsensusInstanceMessage(int consensusInstance, int roundNumber,int senderPID, Content content) {
        super(content);
        _consensusInstance = consensusInstance;
        _roundNumber = roundNumber;
        _senderPID = senderPID;
    }

    @Override
    public String getApplicationMessageType() {
        return super.CONSENSUS_INSTANCE_MESSAGE;
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

    @Override
    public String toString(int level) {
        return toStringWithTabs("ConsensusInstanceMessage: {", level) +
                toStringWithTabs("messageType: " + _messageType, level+1) +
                toStringWithTabs("consensusInstance: " + _consensusInstance, level+1) +
                toStringWithTabs("roundNumber: " + _roundNumber, level+1) +
                toStringWithTabs("senderPID: " + _senderPID, level+1) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }
}
