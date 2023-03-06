package pt.tecnico.blockchain;

import java.io.Serializable;
import java.util.UUID;



public class UdpMessage implements Serializable {
    private String _message;
    private UUID _uuid;

    public UdpMessage(String message) {
        this._message = message;
        this._uuid = UuidGenerator.generateUuid();
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public UUID getUUID() {
        return _uuid;
    }

}
