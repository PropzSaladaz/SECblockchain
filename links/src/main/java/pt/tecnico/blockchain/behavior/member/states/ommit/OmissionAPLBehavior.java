package pt.tecnico.blockchain.behavior.member.states.ommit;


import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.behavior.member.BehaviorController;

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

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException,
            NoSuchAlgorithmException {
        while(BehaviorController.getBehaviorType().equals(OmissionState.TYPE)){
            // Do nothing while on omission state
        }
        return null;
    }
}
