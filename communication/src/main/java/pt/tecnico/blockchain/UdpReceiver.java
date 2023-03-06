package pt.tecnico.blockchain;

import java.io.*;
import java.net.*;
import java.util.UUID;

public class UdpReceiver implements IReceiver{
    private int _localPort;
    private DatagramSocket _socket;

    public UdpReceiver(int localPort) throws UnknownHostException, SocketException {
        setPort(localPort);
        setSocket(new DatagramSocket(getPort()));

    }

    public int getPort() {return _localPort;}

    public void setPort(int _port) {this._localPort = _port;}

    public DatagramSocket getSocket() {return _socket;}

    public void setSocket(DatagramSocket socket) {this._socket = socket;}

    public void closeSocket(){_socket.close();}

    public void receiveMessages() throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        _socket.receive(packet);

        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = new ObjectInputStream(bais);
        UdpMessage message = (UdpMessage) ois.readObject();

        System.out.println(" Receiver: Message received: " + message.getMessage()+ "\n");

        // Send acknowledgement
        InetAddress remoteAddress = packet.getAddress();
        int remotePort = packet.getPort();
        UUID uuid = UUID.fromString(message.getUUID().toString());
        AckMessage ack = new AckMessage(uuid);


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(ack);
        byte[] ackBuffer = baos.toByteArray();
        DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length, remoteAddress, remotePort);
        _socket.send(ackPacket);
    }



}
