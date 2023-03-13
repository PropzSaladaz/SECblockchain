package pt.tecnico.blockchain.Messages;

import java.net.InetAddress;

public class PLMessage extends Message implements Content {

    private int _seqNum;
    private Boolean _ack;
    private InetAddress _senderHostname;
    private int _senderPort;

    public PLMessage(InetAddress senderHostname, int senderPort,Content content) {
        super(content);
        _senderHostname = senderHostname;
        _senderPort = senderPort;

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

    public InetAddress getSenderHostname() {
        return _senderHostname;
    }

    public int getSenderPort() {
        return _senderPort;
    }


}
