package pt.tecnico.blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PerfectLink{

    private Set<UUID> _set;
    FairLossLink _fairLossLink;

    public PerfectLink(Set<UUID> set){
        _set = set;
        _fairLossLink = new FairLossLink();
    }


    public UdpMessage send(DatagramSocket socket, UdpMessage message, String address, int remotePort) throws IOException, ClassNotFoundException {
        //Send Via FLL
        UdpMessage udpMessage = _fairLossLink.send(socket,message,address,remotePort);
        byte[] ackBuffer = new byte[1024];
        DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
        socket.setSoTimeout(5000); // Set a timeout of 5 seconds
        while (true) {
            try {
                socket.receive(ackPacket);
                //ACK received
                AckMessage ackMessage = (AckMessage) CommunicationManager.createMessage(ackBuffer,false);
                if (udpMessage.getUUID().equals(ackMessage.getUUID())) {
                    System.out.println("Sender-> ACK Received \n");
                    return udpMessage;
                }
            } catch (SocketTimeoutException e) {
                // Timeout occurred, NO ACK ,RESEND the message
                System.out.println("Timeout occurred, resending message...\n");
                _fairLossLink.send(socket,message,address,remotePort);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public Map<String, UdpMessage> deliverAuthLink(DatagramSocket socket, DatagramPacket packet,byte[] buffer) throws IOException, ClassNotFoundException {
        Map<String, UdpMessage> result = new HashMap<>();
        UdpMessage udpMessage = _fairLossLink.deliverPLink(socket,packet,buffer);
        if (_set.contains(udpMessage.getUUID())){
            //TO DO not Deliver
            result.put("NO", udpMessage);
            System.out.println("Receiver-> ALREADY DELIVERED \n");

        }else{
            //Not delivered yet
            result.put("YES", udpMessage);
            _set.add(udpMessage.getUUID());

        }
        //Send ACK Message Received
        System.out.println("Receiver-> Message Received: " + udpMessage.getMessage()+ " | Sending Ack..." + "\n");
        InetAddress remoteAddress = packet.getAddress();
        int remotePort = packet.getPort();
        UUID uuid = UUID.fromString(udpMessage.getUUID().toString());
        AckMessage ack = new AckMessage(uuid);
        DatagramPacket ackPacket = CommunicationManager.createPacket(ack,remoteAddress,remotePort);
        socket.send(ackPacket);
        return result;

    }


}
