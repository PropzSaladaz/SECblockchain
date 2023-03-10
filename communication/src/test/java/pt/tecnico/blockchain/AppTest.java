package pt.tecnico.blockchain;


import org.junit.Test;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.*;
import java.util.*;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testUdpString() throws InterruptedException, NoSuchAlgorithmException {

        //Process1
        int processId1 =1;
        KeyPair keyPairServer1 = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PublicKey publicKey1 = keyPairServer1.getPublic();
        PrivateKey privateKey1 = keyPairServer1.getPrivate();

        //Process2
        int processId2 =2;
        KeyPair keyPairServer2 = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PublicKey publicKey2 = keyPairServer2.getPublic();
        PrivateKey privateKey2 = keyPairServer2.getPrivate();


        Thread receiverThread = new Thread(() -> {
            try {
                //Create Sets of uuid, Update List of PublicKeys
                Map<Integer, PublicKey> keys = new HashMap<>();
                keys.put(processId2,publicKey2);
                Set<UUID> uuidSet = new HashSet<>();

                //Deliver wait with AuthLink
                ILink AuthenticatedPerfectLink= new AuthenticatedPerfectLink(keys,uuidSet,privateKey1);
                AuthenticatedPerfectLink.deliver(new DatagramSocket(5001));
            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });

        Thread senderThread = new Thread(() -> {
            try {
                //Create Sets of uuid, Update List of PublicKeys
                Map<Integer, PublicKey> keys = new HashMap<>();
                Set<UUID> uuidSet = new HashSet<>();
                keys.put(processId1,publicKey1);

                //Create AuthLink and Message
                ILink authenticatedPerfectLink= new AuthenticatedPerfectLink(keys,uuidSet,privateKey2);
                UUID uuid = UuidGenerator.generateUuid();
                String message = "Sidnei nao responde";
                UdpMessage udpMessage = new UdpMessage(message,uuid,processId2);

                //Send Message with AuthLink
                authenticatedPerfectLink.send(new DatagramSocket(5000, InetAddress.getByName("localhost")),udpMessage,"localhost",5001);
            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });

        //Start Threads
        receiverThread.start();
        senderThread.start();

        //WAIT
        receiverThread.join();
        senderThread.join();
    }

}
