package pt.tecnico.blockchain.Messages.links;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.Message;

import java.util.Base64;

public class APLMessage extends Message implements Content {

    private byte[] _signature;
    private String _source;
    private String _dest;
    private int _senderPID;


    public APLMessage() {

    }

    public APLMessage(Content content, String source, int senderPID, String dest) {
        super(content);
        _source = source;
        _dest = dest;
        _senderPID = senderPID;
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

    public String getDest() {
        return _dest;
    }

    public void setSource(byte[] source) {
        _signature = source;
    }

    public int getSenderPID() {
        return _senderPID;
    }

    @Override
    public String toString(int level) {
        return  toStringWithTabs("APLMessage {", level) +
                toStringWithTabs("signature: " + Crypto.base64(_signature, 15), level + 1) +
                toStringWithTabs("source: " + _source, level + 1) +
                toStringWithTabs("sender_id: " + getSenderPID(), level + 1) +
                getContent().toString(level + 1) +
                toStringWithTabs("}", level);
    }
}
