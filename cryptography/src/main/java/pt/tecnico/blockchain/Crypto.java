package pt.tecnico.blockchain;



import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class Crypto {

    public static String encryptAES(String message, SecretKey key){
        try{
            byte[] plainBytes = message.getBytes();
            final String CIPHER_ALGO = "AES/ECB/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherBytes = cipher.doFinal(plainBytes);
            return Base64.getEncoder().encodeToString(cipherBytes);
        }catch(Exception e){
            throw new RuntimeException("ERROR WHILE ENCRYPTING");
        }

    }

    public static String decryptAES(String message, SecretKey key){
        try{
            byte[] decodedBytes = Base64.getDecoder().decode(message);
            final String CIPHER_ALGO = "AES/ECB/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] cipherBytes = cipher.doFinal(decodedBytes);
            return new String(cipherBytes, StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("ERROR WHILE DECRYPTING");
        }
    }


    public static byte[] encryptRSAPublic(byte[] plainBytes, PublicKey key){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainBytes);
        } catch (Exception e) {
            throw new RuntimeException("ERROR WHILE ENCRYPTING RSA");
        }
    }

    public static byte[] encryptRSAPrivate(byte[] plainBytes, PrivateKey key){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainBytes);
        } catch (Exception e) {
            throw new RuntimeException("ERROR WHILE ENCRYPTING RSA");
        }
    }

    public static byte[] decryptRSAPrivate(byte[] plainBytes, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(plainBytes);
        } catch (BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException |
                IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR WHILE DECRYPTING RSA");
        }
    }

    public static byte[] decryptRSAPublic(byte[] plainBytes, PublicKey key){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(plainBytes);
        } catch (BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException |
                IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR WHILE DECRYPTING RSA");
        }
    }

    public static byte[] digest(byte[] message) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(message);
    }

    public static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String base64(byte[] bytes, int length) {
        return base64(bytes).substring(0, length) + "...";
    }

    public static String computeHash(String message,String previousBlockHash) throws NoSuchAlgorithmException {
        String concatenated = message+previousBlockHash;
        byte[] hash = digest(concatenated.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(hash);
    }


}