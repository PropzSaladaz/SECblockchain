package pt.tecnico.blockchain;

public class MessageManager {

    public static DatagramPacket createPacket(Message obj, InetAddress address, int remotePort) throws IOException {
        ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(bytesOS);
        objectOS.writeObject(obj);
        byte[] buffer = bytesOS.toByteArray();
        return new DatagramPacket(buffer, buffer.length, address, remotePort);
    }

    public static Message createMessage(byte[] buffer, boolean bool) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bytesIS = new ByteArrayInputStream(buffer);
        ObjectInputStream objectIS = new ObjectInputStream(bytesIS);
        return (Message) objectIS.readObject();
    }
}
