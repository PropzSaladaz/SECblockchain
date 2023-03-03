package pt.tecnico.blockchain;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyConverter {
    public static String keyToString(Key pub) {
        return Base64.getEncoder().encodeToString(pub.getEncoded());
    }

    public static PublicKey base64ToPublicKey(String pub) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(pub);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static SecretKey base64ToSecretKey(String pub) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(pub);
        SecretKeyFactory kf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return kf.generateSecret(new SecretKeySpec(bytes, "AES"));
    }

    public static SecretKey bytesToSecretKey(byte[] bytes) throws Exception {
        return new SecretKeySpec(bytes, "AES");
    }
}
