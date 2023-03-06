package pt.tecnico.blockchain;

import java.io.Serializable;
import java.util.UUID;

public class AckMessage implements Serializable {
    private UUID _uuid;

    public AckMessage(UUID uuid) {
        this._uuid =uuid;
    }

    public UUID getUUID() {
        return _uuid;
    }

    public void setUUID(UUID uuid) {
        _uuid = uuid;
    }


}