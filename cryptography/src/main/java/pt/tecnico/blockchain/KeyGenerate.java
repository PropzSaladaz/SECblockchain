package pt.tecnico.blockchain;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEKeySpec;

import javax.crypto.KeyGenerator;
import java.security.Key;

public class KeyGenerate {

    public static SecretKey generateKey(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] password = token.toCharArray();
        byte[] salt = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
        int iterationCount = 1000;
        int keyLength = 128;
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), "AES");
        return key;
    }

    public static byte[] generate_sharedKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // specify the key size
        Key key = keyGenerator.generateKey();
        return key.getEncoded();
    }

    public static KeyPair generateRSAkeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        return keyPairGenerator.generateKeyPair();
    }

}