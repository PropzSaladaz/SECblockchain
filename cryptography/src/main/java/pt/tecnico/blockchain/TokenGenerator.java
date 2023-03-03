package pt.tecnico.blockchain;

import java.security.SecureRandom;

public class TokenGenerator {
    public static String generateRandom(int length){
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char)('A' + random.nextInt(26)));
        }
        return sb.toString();
    }
}
