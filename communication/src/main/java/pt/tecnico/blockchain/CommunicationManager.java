package pt.tecnico.blockchain;

import java.io.*;
import java.net.*;

public class CommunicationManager {


    public static DatagramPacket createPacket(Object obj, InetAddress address, int remotePort) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] buffer = baos.toByteArray();
        return new DatagramPacket(buffer, buffer.length, address, remotePort);
    }

    public static Object createMessage(byte[] buffer,boolean bool) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    public static void closeSocket(DatagramSocket socket){socket.close();}


}
