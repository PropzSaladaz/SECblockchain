package pt.tecnico.blockchain;

import java.io.*;
import java.net.*;

public class CommunicationManager {

    public static DatagramPacket createPacket(Object obj, InetAddress address, int remotePort) throws IOException {
        ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(bytesOS);
        objectOS.writeObject(obj);
        byte[] buffer = bytesOS.toByteArray();
        return new DatagramPacket(buffer, buffer.length, address, remotePort);
    }

    public static Object createMessage(byte[] buffer, boolean bool) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bytesIS = new ByteArrayInputStream(buffer);
        ObjectInputStream objectIS = new ObjectInputStream(bytesIS);
        return objectIS.readObject();
    }

    public static void closeSocket(DatagramSocket socket){socket.close();}


}
