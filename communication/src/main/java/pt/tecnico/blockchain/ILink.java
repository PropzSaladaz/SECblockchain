package pt.tecnico.blockchain;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.*;

public interface ILink {
    public void send(DatagramSocket socket, DatagramPacket packet, UdpMessage udpMessage) throws IOException ;
    public void deliver(DatagramSocket socket, DatagramPacket packet, UdpMessage udpMessage);
}
