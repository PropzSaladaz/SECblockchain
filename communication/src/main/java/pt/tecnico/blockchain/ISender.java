package pt.tecnico.blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public interface ISender {
    public void sendMessage(String message, String address, int remotePort) throws IOException;


}
