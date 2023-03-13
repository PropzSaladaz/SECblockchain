package pt.tecnico.blockchain.Messages;

import java.net.InetAddress;

public class APLMessage extends Message implements Content{
    
    private byte[] _signature;
    private String _source;
    private int _senderId;


    public APLMessage(Content content,String source) {
        super(content);
        _source = source;
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


}
