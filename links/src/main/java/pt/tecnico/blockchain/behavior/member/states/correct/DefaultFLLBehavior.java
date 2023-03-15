package pt.tecnico.blockchain.behavior.member.states.correct;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.links.FLLMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DefaultFLLBehavior {

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        try {
            FLLMessage message = new FLLMessage(content);
            System.out.println("Sending FLL message: \n" + message.toString());
            socket.send(MessageManager.createPacket(message, hostname, port));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        System.out.println("Waiting for FLL messages...");
        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        FLLMessage message = MessageManager.createMessage(packet.getData());
        System.out.println("FLL message received: \n" + message.toString());
        return message.getContent();
    }
}
