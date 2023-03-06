package pt.tecnico.blockchain;

import java.util.UUID;

public class UuidGenerator {
    public static UUID generateUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid;
    }
}