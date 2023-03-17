package pt.tecnico.blockchain.behavior.member.states.signas;

import pt.tecnico.blockchain.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.APLMessage;
import pt.tecnico.blockchain.PerfectLink;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

public class SignAsAPLBehavior {
    public static void send(DatagramSocket socket, Content content, String hostname, int port, int signAs) {
        try {
            String dest = hostname + ":" + port;
            byte[] encryptedMessage = AuthenticatedPerfectLink.authenticate(content, dest,
                    RSAKeyStoreById.getPrivateKey(AuthenticatedPerfectLink.getId()));
            APLMessage message = new APLMessage(content, AuthenticatedPerfectLink.getSource(),
                    signAs);

            message.setSignature(encryptedMessage);

            System.out.println("SIGNAS: Sending APL with sender id = " + signAs);
            PerfectLink.send(socket, message, InetAddress.getByName(hostname), port);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

}
