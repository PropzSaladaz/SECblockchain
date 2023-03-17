package pt.tecnico.blockchain;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.io.FileInputStream;

public class AESKeyReader {

    public static SecretKey readSharedKey(String keyPath) throws Exception {
        try {
            FileInputStream keyIn = new FileInputStream(keyPath);
            byte[] keyBytes = new byte[keyIn.available()];
            keyIn.read(keyBytes);
            keyIn.close();    
            return new SecretKeySpec(keyBytes, "AES");
        
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
