package pt.tecnico.blockchain.behavior.states.corrupt;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.links.FLLMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CorruptFLLBehavior {
    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        try {
            FLLMessage message = new FLLMessage(content);
            System.out.println("CORRUPTED: Sending FLL message: \n" + message + " to 127.0.0.1:10005");
            socket.send(MessageManager.createPacket(message, InetAddress.getByName("127.0.0.1"), 10005));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
