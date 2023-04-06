package pt.tecnico.blockchain;



import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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

    public static MessageDigest getDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256");

    }

    public static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decodeBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
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

    public static Signature getPrivateSignatureInstance(PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        return signature;
    }

    public static Signature getPublicSignatureInstance(PublicKey publicKey)
            throws InvalidKeyException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        return signature;
    }


    public static byte[] getSignature(byte[] contentBytes, PrivateKey privateKey) 
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(contentBytes);
        return signature.sign();
    }

    public static boolean verifySignature(byte[] contentBytes, byte[] digitalSignature, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(contentBytes);
            return signature.verify(digitalSignature);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR WHILE DECRYPTING RSA");
        }
    }

    public static String getHashFromKey(PublicKey key) throws NoSuchAlgorithmException {
        return base64(digest(key.getEncoded()));
    }

    public static byte[] base64Decode(String input) {
        return Base64.getDecoder().decode(input);
    }
    public static PublicKey getPublicKeyFromHash(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = base64Decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
}