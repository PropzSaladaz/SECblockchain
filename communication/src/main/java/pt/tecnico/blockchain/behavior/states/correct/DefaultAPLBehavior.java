package pt.tecnico.blockchain.behavior.states.correct;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.links.APLReturnMessage;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.APLMessage;
import pt.tecnico.blockchain.links.PerfectLink;
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
            message.sign(AuthenticatedPerfectLink.getId());
            PerfectLink.send(socket, message, InetAddress.getByName(hostname), port);
        } catch (Exception e) {
            Logger.logError("Exception caught in APL send:", e);
        }
    }

    /**
     * Only returns if received a valid APL message.
     * If the message is invalid simply ignore it and wait for a valid one.
     */
    public static APLReturnMessage deliver(DatagramSocket socket) throws IOException, ClassNotFoundException,
            NoSuchAlgorithmException {
        while(true){
            try{
                APLMessage message = (APLMessage) PerfectLink.deliver(socket);
                PublicKey pk = RSAKeyStoreById.getPublicKey(message.getSenderPID());
                if (pk != null && AuthenticatedPerfectLink.validateMessage(message, pk)) {
                    return new APLReturnMessage(message.getContent(), message.getSenderPID());
                }
                Logger.logWarning("Unauthenticated message received, ignoring message " + message.toString(0));
            } catch(Exception e){
                Logger.logError("Exception caught in APL deliver:", e);
            }
        }
    }

}
