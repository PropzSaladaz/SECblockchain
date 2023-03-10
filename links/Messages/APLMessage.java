package pt.tecnico.blockchain;

public class APLMessage extends PLMessage {
    
    private byte[] _signature;

    public ALMessage(String message, int sender) {
        super(message, sender);
    }

    public ALMessage(String message, int sender, byte[] signature) {
        super(message, sender);
        _signature = signature;
    }

    public byte[] getSignatureBytes() {
        return _signature;
    }

    public void setSignature(byte[] signature) {
        _signature = signature;
    }
}
