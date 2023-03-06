package pt.tecnico.blockchain;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class UdpSender implements ISender{
    private InetAddress _localAddress;
    private int _localPort;
    private DatagramSocket _socket;

    public UdpSender(String localAddress, int localPort) throws UnknownHostException, SocketException {
        setAddress(localAddress);
        setPort(localPort);
        setSocket(new DatagramSocket(getPort(), getAddress()));

    }

    public InetAddress getAddress() {return _localAddress;}

    public void setAddress(String address) throws UnknownHostException {
        _localAddress = InetAddress.getByName(address);
    }
    public int getPort() {return _localPort;}

    public void setPort(int _port) {this._localPort = _port;}

    public DatagramSocket getSocket() {return _socket;}

    public void setSocket(DatagramSocket socket) {this._socket = socket;}

    public void closeSocket(){_socket.close();}

    public void sendMessage(String message, String address, int remotePort) throws IOException {
        //Sends Message
        InetAddress remoteAddress = InetAddress.getByName(address);
        UdpMessage udpMessage = new UdpMessage(message);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(udpMessage);
        byte[] buffer = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, remoteAddress, remotePort);
        _socket.send(packet);
        System.out.println("Sender: Message sent: " + udpMessage.getMessage()+ "\n");

        //receives ACk
        byte[] ackBuffer = new byte[1024];
        DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
        _socket.setSoTimeout(5000); // Set a timeout of 5 seconds
        while (true) {
            try {
                _socket.receive(ackPacket);
                ByteArrayInputStream bais = new ByteArrayInputStream(ackBuffer);
                ObjectInputStream ois = new ObjectInputStream(bais);
                AckMessage ackMessage = (AckMessage) ois.readObject();
                if (udpMessage.getUUID().equals(ackMessage.getUUID())) {
                    System.out.println("Acknowledgement received for message: ");
                    break;
                }
            } catch (SocketTimeoutException e) {
                // Timeout occurred, resend the message
                System.out.println("Timeout occurred, resending message...");
                _socket.send(packet);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}


