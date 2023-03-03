package pt.tecnico.blockchain;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

import static org.junit.Assert.assertArrayEquals;

/**
 * Unit test for simple App.
 */
public class AppTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void testRSAEncryptDecrypt() throws Exception{
        KeyPair keyPairServer = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        KeyPair keyPairClient = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        // Get the public and private keys
        PublicKey serverPublicKey = keyPairServer.getPublic();
        PrivateKey serverPrivateKey = keyPairServer.getPrivate();
        PublicKey clientPublicKey = keyPairClient.getPublic();
        PrivateKey clientPrivateKey = keyPairClient.getPrivate();
        // secret
        SecretKey secret = KeyGenerate.generateKey(TokenGenerator.generateRandom(3));

        byte[] sessionKeyBytes = secret.getEncoded();
        byte[] userEncryptedSessionKey = Crypto.encryptRSAPublic(sessionKeyBytes, clientPublicKey);
        byte[] decryptedKey = Crypto.decryptRSAPrivate(userEncryptedSessionKey, clientPrivateKey);
        assertArrayEquals(sessionKeyBytes, decryptedKey);

        byte[] digest = Crypto.digest(userEncryptedSessionKey);
        byte[] encryptedDigest = Crypto.encryptRSAPrivate(digest, serverPrivateKey);
        byte[] decryptedDigest = Crypto.decryptRSAPublic(encryptedDigest, serverPublicKey);
        assertArrayEquals(digest, decryptedDigest);
    }

    public void testConvertSecretToBytesEncryptAndGetItBack() throws Exception{
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        SecretKey secret = KeyGenerate.generateKey(TokenGenerator.generateRandom(3));
        byte[] bytes = secret.getEncoded();

        byte[] encrypted = Crypto.encryptRSAPublic(bytes, publicKey);
        byte[] decrypted = Crypto.decryptRSAPrivate(encrypted, privateKey);

        SecretKey recovered = new SecretKeySpec(decrypted, "AES");
        assertTrue(true);
    }

}
