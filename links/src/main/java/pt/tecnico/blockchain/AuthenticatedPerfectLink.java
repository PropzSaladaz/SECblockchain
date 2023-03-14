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
import java.util.Base64;


import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.links.APLMessage;
import pt.tecnico.blockchain.Messages.Content;


public class AuthenticatedPerfectLink {

    private static String _source;
    private static RSAKeyStoreById _store;
    private static int _id;

    public static byte[] digestAuth(Content content, String source, String dest) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(bytesOS);
        objectOS.writeObject(content);
        digest.update(bytesOS.toByteArray());
        digest.update(dest.getBytes(StandardCharsets.UTF_8));
        digest.update(source.getBytes(StandardCharsets.UTF_8));
        return digest.digest();
    }

    public static byte[] authenticate(Content content, String dest ,PrivateKey privateKey) throws NoSuchAlgorithmException, IOException {
        byte[] digest = digestAuth(content, _source, dest);
        return Crypto.encryptRSAPrivate(digest,privateKey);
    }

    public static boolean verifyAuth(APLMessage message,PublicKey publicKey) throws NoSuchAlgorithmException, IOException,RuntimeException {
        byte[] digest = digestAuth(message.getContent(), message.getSource(), _source);
        byte[] decryptedDigest = Crypto.decryptRSAPublic(message.getSignatureBytes(), publicKey);
        return Arrays.equals(digest, decryptedDigest);
    }

    public static void send(DatagramSocket socket, Content content, String hostname, int port) throws IOException, NoSuchAlgorithmException {

        String dest = hostname + ":" + port;
        byte[] encryptedMessage = authenticate(content,dest, _store.getPrivateKey(_id));
        APLMessage message = new APLMessage(content, _source, _id);

        message.setSignature(encryptedMessage);

        System.out.println("Sending APL");
        PerfectLink.send(socket,message,InetAddress.getByName(hostname) ,port);

    }
    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
       while(true){
           try{
               System.out.println("Waiting for PL messages...");
               APLMessage message = (APLMessage) PerfectLink.deliver(socket);
               PublicKey pk = _store.getPublicKey(message.getSenderPID());
               if (pk != null && verifyAuth(message, pk)) {
                   return message;
               }
               System.out.println("Unauthenticated message received, ignoring message " + message.toString(0));
           }catch(RuntimeException e){
               System.out.println(e.getMessage());
           }
       }
    }

    public static void setSource(String address, int port) throws UnknownHostException {
        _source = address + ":" + port;
        PerfectLink.setSource(address, port);
    }

    public static void setKeyStore(RSAKeyStoreById store) {
        _store = store;
    }

    public static void setId(int id) {
        _id = id;
    }



}
