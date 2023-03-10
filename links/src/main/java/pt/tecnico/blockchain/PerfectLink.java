package pt.tecnico.blockchain;

import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.*;

import pt.tecnico.blockchain.Messages.*;

public class PerfectLink {

    private int seqNum = 0;

    public void send(DatagramSocket socket, PLMessage message, InetAddress hostname, int port) throws IOException {

        message.setSeqNum(seqNum++);
        message.setAck(false);
        System.out.println("Sending PL message: " + message.getMessage() + " to: " + message.getSenderPID());
        FairLossLink.send(socket, message, hostname, port);

        // Wait for acknowledgement of the message identified with the seqNum.
        byte[] ackBuffer = new byte[1024];
        DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
        socket.setSoTimeout(5000); // <- CHANGE THIS TIMEOUT TO SOMETHING ELSE
        while (true) {
            try {
                socket.receive(ackPacket);
                PLMessage ackMessage = (PLMessage) MessageManager.createMessage(ackPacket.getData());
                if (ackMessage.getSeqNum() == seqNum) {
                    System.out.println("PL message received: " + message.getMessage() + " from: " + 
                        message.getSenderPID() + " with seqNum: " + message.getSeqNum() + "; isAck? " + message.isAck());
                    return;
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout occurred, resending message...\n");
                FairLossLink.send(socket, message, hostname, port);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public PLMessage deliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        System.out.println("Waiting for PL messages...");
        PLMessage message = (PLMessage) FairLossLink.deliver(socket);
        // PLDeliver
        System.out.println("PL message received: " + message.getMessage() + " from: " + 
            message.getSenderPID() + " with seqNum: " + message.getSeqNum() + "; isAck? " + message.isAck());
        
        System.out.println("Preparing Ack for PL Message with seqNum: " + message.getSeqNum() + " from: " + message.getSenderPID());
        message.setAck(true);
        message.setSeqNum(message.getSeqNum()+1);
        FairLossLink.send(socket, message, message.getSenderHostname(), message.getSenderPort());
        return message;
    }
}
