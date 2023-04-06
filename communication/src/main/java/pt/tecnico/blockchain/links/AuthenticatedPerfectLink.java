package pt.tecnico.blockchain.links;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Arrays;


import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.links.APLMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.APLReturnMessage;
import pt.tecnico.blockchain.behavior.LinkBehaviorController;


public class AuthenticatedPerfectLink {

    public static long ALLOWED_MESSAGE_WAITING = 1000; // 1 second

    private static String _source;
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

    public static boolean validateMessage(APLMessage message, PublicKey pk) {
        return verifyAuth(message, pk) && verifyTimestamp(message);
    }

    public static void send(DatagramSocket socket, Content content, String hostname, int port) throws IOException, NoSuchAlgorithmException {
        LinkBehaviorController.APLsend(socket, content, hostname, port);
    }

    public static APLReturnMessage deliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        return LinkBehaviorController.APLdeliver(socket);
    }

    public static void setSource(String address, int port) throws UnknownHostException {
        _source = address + ":" + port;
        PerfectLink.setSource(address, port);
    }

    public static void setId(int id) {
        _id = id;
    }

    public static int getId() {
        return _id;
    }

    public static String getSource() {
        return _source;
    }

    private static boolean verifyAuth(APLMessage message,PublicKey publicKey) throws RuntimeException {
        return Crypto.verifySignature(message.digestMessageFields(), message.getSignatureBytes(), publicKey);
    }

    private static boolean verifyTimestamp(APLMessage m) {
        return Instant.now().toEpochMilli() - m.getTimestamp() < ALLOWED_MESSAGE_WAITING;
    }

}
