package pt.tecnico.blockchain.logger;

import java.time.LocalDateTime;

public class Logger {
    public static void logWithTime(String message) {
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println(currentTime + ":  "  + message);
    }
}
