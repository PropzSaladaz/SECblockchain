package pt.tecnico.blockchain.Messages.links;

import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.Message;
import pt.tecnico.blockchain.Messages.MessageManager;

import java.io.IOException;
import java.security.MessageDigest;
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

    @Override
    public byte[] digestMessageFields() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(MessageManager.getContentBytes(this.getContent()));
            digest.update(_dest.getBytes());
            digest.update(_source.getBytes());
            digest.update(Integer.toString(_senderPID).getBytes());
            digest.update(Double.toString(_timestamp).getBytes());
            return digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sign(Integer signerPID) {
        try {
            _signature = Crypto.getSignature(digestMessageFields(), RSAKeyStoreById.getPrivateKey(signerPID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDest() {
        return _dest;
    }

    public String getSource() {
        return _source;
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
                toStringWithTabs("destination: " + _dest, level + 1) +
                toStringWithTabs("senderId: " + getSenderPID(), level + 1) +
                toStringWithTabs("timestamp: " + _timestamp, level + 1) +
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
                    _dest.equals(m.getDest()) &&
                    _timestamp == m.getTimestamp() &&
                    getContent().equals(m.getContent());
        } catch(ClassCastException e) {
            return false;
        }
    }
}
