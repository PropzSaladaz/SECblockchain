package pt.tecnico.blockchain.Messages.ibft;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import pt.tecnico.blockchain.Keys.RSAKeyStoreById;

import pt.tecnico.blockchain.Messages.ApplicationMessage;

public class ConsensusInstanceMessage extends ApplicationMessage implements Content {

    public static final String PRE_PREPARE = "PRE-PREPARE";
    public static final String PREPARE = "PREPARE";
    public static final String COMMIT = "COMMIT";

    private String _messageType;
    private int _consensusInstance;
    private int _roundNumber;
    private int _senderPID;
    private byte[] _signature;

    public ConsensusInstanceMessage(int consensusInstance, int roundNumber,int senderPID, Content content) {
        super(content);
        _consensusInstance = consensusInstance;
        _roundNumber = roundNumber;
        _senderPID = senderPID;
    }

    @Override
    public String getApplicationMessageType() {
        return CONSENSUS_INSTANCE_MESSAGE;
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

    public void setSenderPID(int value) {
        _senderPID = value;
    }

    public void setSignature(byte[] signature) {
        _signature = signature;
    }

    public byte[] getSignatureBytes() {
        return _signature;
    }

    @Override
    public String toString(){
       return toString(0);
   }

    @Override
    public String toString(int level) {
        return toStringWithTabs("ConsensusInstanceMessage: {", level) +
                toStringWithTabs("signature: " + Crypto.base64(_signature, 15), level+1) +
                toStringWithTabs("messageType: " + _messageType, level+1) +
                toStringWithTabs("consensusInstance: " + _consensusInstance, level+1) +
                toStringWithTabs("roundNumber: " + _roundNumber, level+1) +
                toStringWithTabs("senderPID: " + _senderPID, level+1) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }

    @Override
    public boolean equals(Content another) {
        try {
            ConsensusInstanceMessage m = (ConsensusInstanceMessage) another;
            return _consensusInstance == m.getConsensusInstance() &&
                    _roundNumber == m.getRound() &&
                    _senderPID == m.getSenderPID() &&
                    getContent().equals(m.getContent());
        } catch(ClassCastException e) {
            return false;
        }
    }

    @Override
    public byte[] digestMessageFields() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(MessageManager.getContentBytes(getContent()));
            digest.update(_messageType.getBytes());
            digest.update(Integer.toString(_consensusInstance).getBytes());
            digest.update(Integer.toString(_roundNumber).getBytes());
            digest.update(Integer.toString(_senderPID).getBytes());
            return digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sign(Integer signerPID) {
        try {
            getContent().sign(signerPID);
            _signature = Crypto.getSignature(digestMessageFields(), RSAKeyStoreById.getPrivateKey(signerPID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
