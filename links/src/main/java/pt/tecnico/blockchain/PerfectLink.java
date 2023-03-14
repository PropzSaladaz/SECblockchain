package pt.tecnico.blockchain;

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.UUID;


import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.links.PLMessage;

public class PerfectLink {
    private static UUID seqNum;
    private static InetAddress _address;
    private static int _port;
    private static Map<UUID, PLMessage> _ackMessages;

    public static boolean hasAckArrived(UUID seqNum){
        return _ackMessages.containsKey(seqNum);
    }

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) throws IOException {
        PLMessage message = new PLMessage(_address, _port,  content);
        message.setSeqNum(UuidGenerator.generateUuid());
        message.setAck(false);
        long startTime = System.currentTimeMillis();
        FairLossLink.send(socket,message,hostname,port);
        while (!hasAckArrived(message.getSeqNum())) {
            if (System.currentTimeMillis() - startTime > 5000) {
                System.out.println("Timeout occurred, resending message...\n");
                FairLossLink.send(socket, message, hostname, port);
                startTime = System.currentTimeMillis(); // reset start time
            }
        }
        System.out.println("PL - Ack received");
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        while(true){
            PLMessage message = (PLMessage) FairLossLink.deliver(socket);
            System.out.println("PL - message received");
            if (message.isAck()) {
                System.out.println("PL - message received and is ACK");
                _ackMessages.put(message.getSeqNum(),message);
            }else if (!hasAckArrived(message.getSeqNum())) { // TODO +1 ?
                message.setAck(true);
                message.setSeqNum(message.getSeqNum());
                FairLossLink.send(socket, message, message.getSenderHostname(), message.getSenderPort());
                return message.getContent();
            }
        }
    }

    public static void setDeliveredMap(Map<UUID, PLMessage> ackMessages) {
        _ackMessages = ackMessages;
    }

    public static void setSource(String address, int port) throws UnknownHostException {
        _address = InetAddress.getByName(address);
        _port = port;
    }





}
