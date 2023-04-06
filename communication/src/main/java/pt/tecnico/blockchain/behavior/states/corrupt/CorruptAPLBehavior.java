package pt.tecnico.blockchain.behavior.states.corrupt;

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

        @Override
        public boolean equals(Content another) {
            return false;
        }
    }

    public static void send(DatagramSocket socket, Content content, String hostname, int port) {

        try {
            String dest = hostname + ":" + port;
            CorruptedMessage corrupted = new CorruptedMessage();

            APLMessage message = new APLMessage(corrupted, AuthenticatedPerfectLink.getSource(), dest,
                    AuthenticatedPerfectLink.getId());
            message.sign(AuthenticatedPerfectLink.getId());
            System.out.println("CORRUPTED: Sending Corrupted APL message");
            PerfectLink.send(socket, message, InetAddress.getByName(hostname), port);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static APLReturnMessage deliver(DatagramSocket socket) throws IOException, ClassNotFoundException,
            NoSuchAlgorithmException {
        while(true){
            try {
                System.out.println("CORRUPTED: Waiting for APL messages...");
                APLMessage message = (APLMessage) PerfectLink.deliver(socket);
                System.out.println("CORRUPTED: returning message (without checking signature)");
                return new APLReturnMessage(message.getContent(), message.getSenderPID());
            }catch(RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
    }

}
