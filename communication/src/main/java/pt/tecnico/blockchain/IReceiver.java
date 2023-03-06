package pt.tecnico.blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public interface IReceiver {
    public void receiveMessages() throws IOException, ClassNotFoundException;

}





