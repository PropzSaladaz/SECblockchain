package pt.tecnico.blockchain.behavior.member.states.correct;

import pt.tecnico.blockchain.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.links.APLMessage;
import pt.tecnico.blockchain.PerfectLink;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;


public class DefaultAPLBehavior {
    public static void send(DatagramSocket socket, Content content, String hostname, int port) {

        try {
            APLMessage message = new APLMessage(content, AuthenticatedPerfectLink.getSource(),
                AuthenticatedPerfectLink.getId(), hostname + ":" + port);

            message.setSignature(Crypto.getSignature(MessageManager.getContentBytes(content), 
                RSAKeyStoreById.getPrivateKey(AuthenticatedPerfectLink.getId())));

            System.out.println("Sending APL");
            PerfectLink.send(socket, message, InetAddress.getByName(hostname), port);

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();

        } catch (InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException,
            NoSuchAlgorithmException {
        while(true){
            try{
                System.out.println("Waiting for PL messages...");
                APLMessage message = (APLMessage) PerfectLink.deliver(socket);
                PublicKey pk = RSAKeyStoreById.getPublicKey(message.getSenderPID());
                if (pk != null && 
                    Crypto.verifySignature(MessageManager.getContentBytes(message.getContent()), message.getSignatureBytes(), pk)) {
                    return message.getContent();
                }
                System.out.println("Unauthenticated message received, ignoring message " + message.toString(0));
            } catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

}
