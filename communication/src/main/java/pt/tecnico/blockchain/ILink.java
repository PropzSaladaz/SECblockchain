package pt.tecnico.blockchain;

import java.net.DatagramSocket;
import java.io.*;
import java.security.NoSuchAlgorithmException;


public interface ILink {

    UdpMessage send(DatagramSocket socket,UdpMessage message, String address, int remotePort) throws IOException, ClassNotFoundException, NoSuchAlgorithmException;

    UdpMessage deliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException;
}
