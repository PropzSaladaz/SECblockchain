package pt.tecnico.blockchain.Messages;

import java.net.InetAddress;

public class PLMessage extends FLLMessage {

    private int _seqNum;
    private Boolean _ack;

    public PLMessage(String message, int sender, InetAddress senderHostname, int senderPort) {
        super(message, sender, senderHostname, senderPort);
    }

    public PLMessage(String message, int sender, InetAddress senderHostname, int senderPort, int seqNum, Boolean ack) {
        super(message, sender, senderHostname, senderPort);
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
