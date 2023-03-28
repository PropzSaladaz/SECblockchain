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
            String dest = hostname + ":" + port;
            APLMessage message = new APLMessage(content, AuthenticatedPerfectLink.getSource(), dest,
                    AuthenticatedPerfectLink.getId());
            message.sign(RSAKeyStoreById.getPrivateKey(AuthenticatedPerfectLink.getId()));
            PerfectLink.send(socket, message, InetAddress.getByName(hostname), port);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Only returns if received a valid APL message.
     * If the message is invalid simply ignore it and wait for a valid one.
     */
    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException,
            NoSuchAlgorithmException {
        while(true){
            try{
                APLMessage message = (APLMessage) PerfectLink.deliver(socket);
                PublicKey pk = RSAKeyStoreById.getPublicKey(message.getSenderPID());
                if (pk != null && AuthenticatedPerfectLink.validateMessage(message, pk)) {
                    return message.getContent();
                }
                System.out.println("Unauthenticated message received, ignoring message " + message.toString(0));
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
