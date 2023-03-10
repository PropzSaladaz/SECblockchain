package pt.tecnico.blockchain;

import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.*;

import pt.tecnico.blockchain.Messages.*;

public class FairLossLink {

    public static void send(DatagramSocket socket, FLLMessage message, InetAddress hostname, int port) throws IOException {
        System.out.println("Sending FLL message: " + message.getMessage() + "to: " + message.getSenderPID());
        socket.send(MessageManager.createPacket(message, hostname, port));
    }

    public static FLLMessage deliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        System.out.println("Waiting for FLL messages...");
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        FLLMessage message = (FLLMessage) MessageManager.createMessage(packet.getData());
        System.out.println("FLL message received: " + message.getMessage() + "from: " + message.getSenderPID());
        return message;
    }
}