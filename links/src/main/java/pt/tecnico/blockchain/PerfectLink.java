package pt.tecnico.blockchain;

import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.*;
import java.util.Map;


import pt.tecnico.blockchain.Messages.*;

public class PerfectLink {
    private static int seqNum = 0;
    private static Map<Integer, PLMessage> _ackMessages;

    public static boolean hasAckArrived(int seqNum){
        return _ackMessages.containsKey(seqNum);
    }

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) throws IOException {
        PLMessage message = new PLMessage(hostname,port,content);
        message.setSeqNum(seqNum++);
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
        System.out.println("Ack received from message: " + message.getContent().toString());
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        while(true){
            PLMessage message = (PLMessage) FairLossLink.deliver(socket);
            if (message.isAck()) {
                _ackMessages.put(message.getSeqNum(),message);
            }else if (!hasAckArrived(message.getSeqNum()+1)) {
                message.setAck(true);
                message.setSeqNum(message.getSeqNum()+1);
                FairLossLink.send(socket, message, message.getSenderHostname(), message.getSenderPort());
                return message.getContent();
            }
        }
    }

    public static void setDeliveredMap(Map<Integer, PLMessage> ackMessages) {_ackMessages = ackMessages;
    }




}
