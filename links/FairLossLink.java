package pt.tecnico.blockchain;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.*;

public class FairLossLink {

    public static void send(DatagramSocket socket, Message message) throws IOException {
        System.out.println("Sending FLL message: " + message.getMessage() "to: " + message.getSender());
        socket.send(MessageManager.createPacket(message));
    }

    public static FLLMessage deliver(DatagramSocket socket, DatagramPacket packet) {
        System.out.println("Waiting for FLL messages...");
        socket.receive(packet);
        FLLMessage message = (FLLMessage) MessageManager.createMessage(packet.getData());
        System.out.println("FLL message received: " + message.getMessage() "from: " + message.getSender());
        return message;
    }
}