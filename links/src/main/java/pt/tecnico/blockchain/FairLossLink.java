package pt.tecnico.blockchain;

import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.*;

import pt.tecnico.blockchain.Messages.*;

public class FairLossLink {


    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port, int senderPID) throws IOException {
        FLLMessage message = new FLLMessage(content, senderPID);
        System.out.println("Sending FLL message: \n" + message.toString());
        socket.send(MessageManager.createPacket(message, hostname, port));
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        System.out.println("Waiting for FLL messages...");
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        FLLMessage message = MessageManager.createMessage(packet.getData());
        System.out.println("FLL message received: \n" + message.toString());
        return message.getContent();
    }


}