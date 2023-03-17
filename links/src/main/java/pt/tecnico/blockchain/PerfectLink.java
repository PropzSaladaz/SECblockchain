package pt.tecnico.blockchain;

import java.net.*;
import java.io.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.links.PLMessage;
import pt.tecnico.blockchain.behavior.member.LinkBehaviorController;

public class PerfectLink {
    public static final int RESEND_MESSAGE_TIMEOUT = 5000;

    private static final ConcurrentHashMap<UUID, PLMessage> _ackMessages = new ConcurrentHashMap<>();
    private static InetAddress _address;
    private static int _port;

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        LinkBehaviorController.PLsend(socket, content, hostname, port);
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        return LinkBehaviorController.PLdeliver(socket);
    }


    public static boolean hasAckArrived(UUID seqNum){
        return _ackMessages.containsKey(seqNum);
    }

    public static void sendAck(DatagramSocket socket, PLMessage message) {
        message.setAck(true);
        message.setUUID(message.getUUID());
        FairLossLink.send(socket, message, message.getSenderHostname(), message.getSenderPort());
    }

    public static void setSource(String address, int port) throws UnknownHostException {
        _address = InetAddress.getByName(address);
        _port = port;
    }

    public static void putACK(UUID id, PLMessage message) {
        _ackMessages.put(id, message);
    }

    public static InetAddress getAddress() {
        return _address;
    }

    public static int getPort() {
        return _port;
    }

}
