package pt.tecnico.blockchain.Messages.links;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.Message;

import java.net.InetAddress;

public class PLMessage extends Message implements Content {

    private int _seqNum;
    private Boolean _ack;
    private InetAddress _senderHostname;
    private int _senderPort;

    public PLMessage() {
    }

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

    @Override
    public String toString(int level) {
        return  toStringWithTabs("PLMessage: {", level) +
                toStringWithTabs("seq_number: " + _seqNum, level + 1) +
                toStringWithTabs("Ack: " + _ack, level + 1) +
                toStringWithTabs("sender_hostname: " + _senderHostname, level + 1) +
                toStringWithTabs("sender_port: " + _senderPort, level + 1) +
                getContent().toString(level + 1) +
                toStringWithTabs("}", level);
    }

    @Override
    public boolean equals(Content another) {
        try {
            PLMessage m = (PLMessage) another;
            return _seqNum == m.getSeqNum() &&
                    _ack == m.isAck() &&
                    _senderHostname == m.getSenderHostname() &&
                    _senderPort == m.getSenderPort() &&
                    getContent().equals(m.getContent());
        } catch(ClassCastException e) {
            return false;
        }
    }
}
