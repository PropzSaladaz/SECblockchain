package pt.tecnico.blockchain.Messages.links;

import pt.tecnico.blockchain.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.Message;
import pt.tecnico.blockchain.Messages.MessageManager;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Arrays;


public class APLMessage extends Message implements Content {

    private byte[] _signature;
    private String _source;
    private String _dest;
    private int _senderPID;
    private long _timestamp;


    public APLMessage() {

    }

    public APLMessage(Content content, String source, String dest, int senderPID) {
        super(content);
        _source = source;
        _senderPID = senderPID;
        _dest = dest;
        _timestamp = Instant.now().toEpochMilli();
    }

    public byte[] getSignatureBytes() {
        return _signature;
    }

    public void sign(PrivateKey key) throws NoSuchAlgorithmException, IOException {
        _signature = AuthenticatedPerfectLink.authenticate(getContent(), _dest, key);
    }

    public String getSource() {
        return _source;
    }

    public void setSource(byte[] source) {
        _signature = source;
    }

    public int getSenderPID() {
        return _senderPID;
    }

    public long getTimestamp() {
        return _timestamp;
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

    @Override
    public boolean equals(Content another) {
        try {
            APLMessage m = (APLMessage) another;
            return Arrays.equals(_signature, m.getSignatureBytes()) &&
                    _source.equals(m.getSource()) &&
                    _senderPID == m.getSenderPID() &&
                    getContent().equals(m.getContent());
        } catch(ClassCastException e) {
            return false;
        }
    }
}
