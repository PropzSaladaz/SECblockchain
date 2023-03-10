package pt.tecnico.blockchain;

public class PLMessage extends FLLMessage {

    private int _seqNum;
    private Boolean _ack;

    public PLMessage(String message, int sender) {
        super(message, sender);
    }

    public PLMessage(String message, int sender, int seqNum, Boolean ack) {
        super(message, sender);
        _seqNum = seqNum;
        _ack = ack;
    }

    public int getSeqNum() {
        return _seqNum;
    }

    public void setSeqNum(int value) {
        _seqNum = value;
    }

    public Boolean isAck() {
        return _ack;
    }

    public void setAck(Boolean value) {
        _ack = value;
    }
}
