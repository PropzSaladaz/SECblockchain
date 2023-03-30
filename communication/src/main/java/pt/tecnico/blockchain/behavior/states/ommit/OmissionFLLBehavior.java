package pt.tecnico.blockchain.behavior.states.ommit;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.links.FLLMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class OmissionFLLBehavior {
    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        System.out.println("Omitting FLL send");
    }
}
