package pt.tecnico.blockchain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Map;



import pt.tecnico.blockchain.Messages.APLMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Pair;


public class AuthenticatedPerfectLink {

    private static int _id;
    private static String _source;

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

    public static void send(DatagramSocket socket, Content content, PrivateKey myPrivKey, InetAddress hostname, int port) throws IOException, NoSuchAlgorithmException {

        String dest = hostname.toString() + port;
        byte[] encryptedMessage = authenticate(content,dest,myPrivKey);
        APLMessage message = new APLMessage(content,_source);

        message.setSignature(encryptedMessage);

        System.out.println("Sending APL" + message.getContent().toString() + "to :" + dest);
        PerfectLink.send(socket,message,hostname,port);

    }
    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
       while(true){
           try{
               System.out.println("Waiting for PL messages...");
               APLMessage message = (APLMessage) PerfectLink.deliver(socket);
               System.out.println("PL message received: " + message.getContent().toString() + " from: " + message.getSenderID());
               //TO DO CHECK KEY FIRST
               if (verifyAuth(message,createKeys.getPublic(message.getSenderID()))){
                   return message;
               }
           }catch(RuntimeException e){
               System.out.println(e.getMessage());
           }
       }
    }

    public static void setSource(String address, int port) {
        _source = address + port;

    }

    public static void setId(Integer id) { _id = id;}



}
