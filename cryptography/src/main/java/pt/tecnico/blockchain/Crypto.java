package pt.tecnico.blockchain;



import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;


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

    public static byte[] getSignature(byte[] contentBytes, PrivateKey privateKey, String source, String dest) 
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(contentBytes);
        signature.update(source.getBytes());
        signature.update(dest.getBytes());
        return signature.sign();
    }

    public static Signature getSignatureInstance(PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        return signature;
    }

    public static byte[] getSignature(byte[] contentBytes, PrivateKey privateKey) 
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(contentBytes);
        return signature.sign();
    }

    public static boolean verifySignature(byte[] contentBytes, byte[] digitalSignature, PublicKey publicKey) 
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(contentBytes);
        return signature.verify(digitalSignature);
    }

    public static String getHashFromKey(PublicKey key) throws NoSuchAlgorithmException {
        return base64(digest(key.getEncoded()));
    }
}