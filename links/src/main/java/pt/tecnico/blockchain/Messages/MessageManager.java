package pt.tecnico.blockchain.Messages;

import java.io.*;
import java.net.InetAddress;
import java.net.DatagramPacket;

public class MessageManager {

    public static DatagramPacket createPacket(FLLMessage message, InetAddress address, int remotePort) throws IOException {
        ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(bytesOS);
        objectOS.writeObject(message);
        byte[] buffer = bytesOS.toByteArray();
        return new DatagramPacket(buffer, buffer.length, address, remotePort);
    }

    public static Message createMessage(byte[] buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bytesIS = new ByteArrayInputStream(buffer);
        ObjectInputStream objectIS = new ObjectInputStream(bytesIS);
        return (Message) objectIS.readObject();
    }
}
