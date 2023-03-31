package pt.tecnico.blockchain.behavior.states.ommit;


import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.APLReturnMessage;
import pt.tecnico.blockchain.behavior.LinkBehaviorController;

import java.io.IOException;
import java.net.DatagramSocket;

import java.security.NoSuchAlgorithmException;

/**
 * Omit all messages (sent and received)
 * Only need to omit APL layer, since it calls all the layers below
 */
public class OmissionAPLBehavior {

    public static void send(DatagramSocket socket, Content content, String hostname, int port) {
        System.out.println("Omitting send message in APL (message requested to be sent but was omitted");
    }

    public static APLReturnMessage deliver(DatagramSocket socket) throws IOException, ClassNotFoundException,
            NoSuchAlgorithmException {
        while(LinkBehaviorController.getBehaviorType().equals(OmissionState.TYPE)){
            // Do nothing while on omission state
            try { // When added some delay it gets out of the loop
                Thread.sleep(10);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
