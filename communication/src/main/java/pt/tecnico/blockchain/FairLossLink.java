package pt.tecnico.blockchain;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.*;

public class FairLossLink implements ILink {
    
    public void send(DatagramSocket socket, DatagramPacket packet, UdpMessage udpMessage) throws IOException {
        socket.send(packet);
        System.out.println("Sender: Message sent: " + udpMessage.getMessage()+ "\n");
    }

    public void deliver(DatagramSocket socket, DatagramPacket packet, UdpMessage udpMessage) {

    }
}