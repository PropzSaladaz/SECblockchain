package pt.tecnico.blockchain.behavior.member.states.corrupt;

import pt.tecnico.blockchain.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.APLMessage;
import pt.tecnico.blockchain.PerfectLink;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Crypto;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;

/**
 * Corrupts message content when sending
 * Delivers all messages even if they do not have a valid signature
 */
public class CorruptAPLBehavior {

    private static class CorruptedMessage implements Content {
        private String content = "Corrupted";

        @Override
        public String toString(int tabs) {
            return toStringWithTabs("CorruptedMessage: {", tabs) +
                    toStringWithTabs("message: " + content, tabs+1) +
                    toStringWithTabs("}", tabs);
        }
    }

    public static void send(DatagramSocket socket, Content content, String hostname, int port) {

        try {
            CorruptedMessage corrupted = new CorruptedMessage();
            APLMessage message = new APLMessage(corrupted, AuthenticatedPerfectLink.getSource(),
                AuthenticatedPerfectLink.getId(), hostname + ":" + port);

            message.setSignature(Crypto.getSignature(MessageManager.getContentBytes(content), 
                RSAKeyStoreById.getPrivateKey(AuthenticatedPerfectLink.getId())));

            System.out.println("CORRUPTED: Sending Corrupted APL message");
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
            try {
                System.out.println("CORRUPTED: Waiting for APL messages...");
                APLMessage message = (APLMessage) PerfectLink.deliver(socket);
                PublicKey pk = RSAKeyStoreById.getPublicKey(message.getSenderPID());
                System.out.println("CORRUPTED: returning message (without checking signature)");
                return message.getContent();

            }catch(RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
    }

}
