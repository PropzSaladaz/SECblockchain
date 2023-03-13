package pt.tecnico.blockchain.Messages;

import java.net.InetAddress;
import java.util.Base64;

public class APLMessage extends Message implements Content {

    private byte[] _signature;
    private String _source;
    private int _senderId;


    public APLMessage() {

    }

    public APLMessage(Content content, String source, int id) {
        super(content);
        _source = source;
        _senderId = id;
    }

    public byte[] getSignatureBytes() {
        return _signature;
    }

    public void setSignature(byte[] signature) {
        _signature = signature;
    }

    public String getSource() {
        return _source;
    }

    public void setSource(byte[] source) {
        _signature = source;
    }

    public int getSenderID() {
        return _senderId;
    }

    @Override
    public String toString(int level) {
        String sign20chars = Base64.getEncoder().encodeToString(_signature).substring(0, 15) + "...";
        return  toStringWithTabs("APLMessage {", level) +
                toStringWithTabs("signature: " + sign20chars, level + 1) +
                toStringWithTabs("source: " + _source, level + 1) +
                toStringWithTabs("sender_id: " + _senderId, level + 1) +
                _content.toString(level + 1) +
                toStringWithTabs("}", level);
    }
}
