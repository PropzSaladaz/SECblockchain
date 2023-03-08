package pt.tecnico.blockchain;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;



public class UdpMessage implements Serializable {
    private String _message;
    private UUID _uuid;
    private int _processId;
    private byte[] _encryptMac;
    //private SeqNumber _seq;

    public UdpMessage(String message,UUID uuid,int processId) throws NoSuchAlgorithmException {
        this._processId = processId;
        this._message = message;
        this._uuid = uuid;
        //this._seq = new SeqNumber();
    }

    public String getMessage() {
        return _message;
    }

    public UUID getUUID() {
        return _uuid;
    }

    public int getProcessId() {
        return _processId;
    }

    public byte[] getEncryptedMac() { return _encryptMac;}

    public void setEncryptMac(byte[] encryptMac){
        _encryptMac = encryptMac;

    }

    /*public Integer getSeq() {
        return _seq.getSeq();
    }*/

}
