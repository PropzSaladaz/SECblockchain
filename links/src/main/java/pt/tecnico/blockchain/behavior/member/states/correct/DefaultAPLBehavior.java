package pt.tecnico.blockchain.behavior.member.states.correct;

import pt.tecnico.blockchain.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.APLMessage;
import pt.tecnico.blockchain.PerfectLink;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class DefaultAPLBehavior {
    public static void send(DatagramSocket socket, Content content, String hostname, int port) {

        try {
            String dest = hostname + ":" + port;
            byte[] encryptedMessage = AuthenticatedPerfectLink.authenticate(content, dest,
                    AuthenticatedPerfectLink.getStore().getPrivateKey(AuthenticatedPerfectLink.getId()));
            APLMessage message = new APLMessage(content, AuthenticatedPerfectLink.getSource(),
                    AuthenticatedPerfectLink.getId());

            message.setSignature(encryptedMessage);

            System.out.println("Sending APL");
            PerfectLink.send(socket, message, InetAddress.getByName(hostname), port);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }


    }
    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException,
            NoSuchAlgorithmException {
        while(true){
            try{
                System.out.println("Waiting for PL messages...");
                APLMessage message = (APLMessage) PerfectLink.deliver(socket);
                PublicKey pk = AuthenticatedPerfectLink.getStore().getPublicKey(message.getSenderPID());
                if (pk != null && AuthenticatedPerfectLink.verifyAuth(message, pk)) {
                    return message.getContent();
                }
                System.out.println("Unauthenticated message received, ignoring message " + message.toString(0));
            }catch(RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
    }

}
