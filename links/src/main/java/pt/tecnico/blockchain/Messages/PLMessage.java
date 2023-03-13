package pt.tecnico.blockchain.Messages;

import java.net.InetAddress;
import java.util.UUID;

public class PLMessage extends Message implements Content {

    private UUID _seqNum;
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
    public UUID getSeqNum() {
        return _seqNum;
    }

    public void setSeqNum(UUID value) {
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
                _content.toString(level + 1) +
                toStringWithTabs("}", level);
    }


}
