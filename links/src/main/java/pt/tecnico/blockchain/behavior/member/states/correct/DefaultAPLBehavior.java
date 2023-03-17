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
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;


public class DefaultAPLBehavior {
    public static void send(DatagramSocket socket, Content content, String hostname, int port) {

        try {
            System.out.println("here i am");
            String dest = hostname + ":" + port;
            byte[] encryptedMessage = AuthenticatedPerfectLink.authenticate(content, dest,
                    RSAKeyStoreById.getPrivateKey(AuthenticatedPerfectLink.getId()));
            APLMessage message = new APLMessage(content, AuthenticatedPerfectLink.getSource(),
                    AuthenticatedPerfectLink.getId());

            message.setSignature(encryptedMessage);

            PerfectLink.send(socket, message, InetAddress.getByName(hostname), port);
            System.out.println("T");

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException,
            NoSuchAlgorithmException {
        while(true){
            try{
                APLMessage message = (APLMessage) PerfectLink.deliver(socket);
                PublicKey pk = RSAKeyStoreById.getPublicKey(message.getSenderPID());
                if (pk != null && AuthenticatedPerfectLink.verifyAuth(message, pk)) {
                    return message.getContent();
                }
                System.out.println("Unauthenticated message received, ignoring message " + message.toString(0));
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
