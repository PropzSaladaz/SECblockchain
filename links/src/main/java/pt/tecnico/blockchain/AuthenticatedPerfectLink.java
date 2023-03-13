package pt.tecnico.blockchain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.Arrays;


import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.APLMessage;
import pt.tecnico.blockchain.Messages.Content;


public class AuthenticatedPerfectLink {

    private static String _source;
    private static RSAKeyStoreById _store;

    public static byte[] digestAuth(Content content, String dest) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(bytesOS);
        objectOS.writeObject(content);
        digest.update(bytesOS.toByteArray());
        digest.update(dest.getBytes(StandardCharsets.UTF_8));
        digest.update(_source.getBytes(StandardCharsets.UTF_8));
        return digest.digest();
    }

    public static byte[] authenticate(Content content, String dest ,PrivateKey privateKey) throws NoSuchAlgorithmException, IOException {
        byte[] digest = digestAuth(content,dest);
        return Crypto.encryptRSAPrivate(digest,privateKey);
    }

    public static boolean verifyAuth(APLMessage message,PublicKey publicKey) throws NoSuchAlgorithmException, IOException,RuntimeException {
        byte[] digest = digestAuth(message.getContent(),message.getSource());
        byte[] decryptedDigest = Crypto.decryptRSAPublic(message.getSignatureBytes(), publicKey);
        return Arrays.equals(digest, decryptedDigest);
    }

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port, int senderPID) throws IOException, NoSuchAlgorithmException {

        String dest = hostname.toString() + port;
        byte[] encryptedMessage = authenticate(content,dest, _store.getPrivateKey(senderPID));
        APLMessage message = new APLMessage(content, _source, senderPID);

        message.setSignature(encryptedMessage);

        System.out.println("Sending APL");
        PerfectLink.send(socket,message,hostname,port,senderPID);

    }
    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
       while(true){
           try{
               System.out.println("Waiting for PL messages...");
               APLMessage message = (APLMessage) PerfectLink.deliver(socket);
               // TODO CHECK KEY FIRST
               if (verifyAuth(message, _store.getPublicKey(message.getSenderPID()))){
                   return message;
               }
           }catch(RuntimeException e){
               System.out.println(e.getMessage());
           }
       }
    }

    public static void setSource(String address, int port) throws UnknownHostException {
        _source = address + port;
        PerfectLink.setSource(address, port);
    }

    public static void setKeyStore(RSAKeyStoreById store) {
        _store = store;
    }



}
