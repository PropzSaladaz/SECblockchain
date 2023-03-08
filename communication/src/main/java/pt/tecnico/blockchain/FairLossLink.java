package pt.tecnico.blockchain;

import java.io.*;
import java.net.*;


public class FairLossLink {


    public UdpMessage send(DatagramSocket socket,UdpMessage udpMessage,String address, int remotePort) throws IOException {
        DatagramPacket packet = CommunicationManager.createPacket(udpMessage,InetAddress.getByName(address),remotePort);
        socket.send(packet);
        System.out.println("Sender-> Message sent: " + udpMessage.getMessage()+ "\n");
        return udpMessage;
    }


    public UdpMessage deliverPLink(DatagramSocket socket, DatagramPacket packet,byte[] buffer) throws IOException, ClassNotFoundException {
        System.out.println("Receiver-> Waiting for messages...\n");
        socket.receive(packet);
        UdpMessage udpMessage = (UdpMessage) CommunicationManager.createMessage(buffer,true);
        return udpMessage;


    }


}


