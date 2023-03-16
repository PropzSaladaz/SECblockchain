package pt.tecnico.blockchain;

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.links.APLMessage;
import pt.tecnico.blockchain.Messages.links.PLMessage;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;

public class PerfectLink {
    private static final int ASSUME_FAILURE_TIMEOUT = 5000;
    private static final int RESEND_MESSAGE_TIMEOUT = 2000;

    private static InetAddress _address;
    private static int _port;
    private static final ConcurrentHashMap<UUID, PLMessage> _ackMessages = new ConcurrentHashMap<>();

    public static boolean hasAckArrived(UUID seqNum){
        return _ackMessages.containsKey(seqNum);
    }

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) throws IOException {
        PLMessage message = new PLMessage(_address, _port,  content);
        message.setUUID(UuidGenerator.generateUuid());
        message.setAck(false);
        //System.out.println("Sending to: " + port);
        ScheduledTask task = new ScheduledTask( () -> {
            FairLossLink.send(socket, message, hostname , port);
        }, RESEND_MESSAGE_TIMEOUT);
        task.setStopCondition(() -> hasAckArrived(message.getUUID()));
        task.start();
    }


    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        while(true){
            PLMessage message = (PLMessage) FairLossLink.deliver(socket);
            //System.out.println("PL - message received");
            if (message.isAck()) {
                //System.out.println("PL - message received and is ACK");
                _ackMessages.put(message.getUUID(),message);
            }else if (!hasAckArrived(message.getUUID())) {
                message.setAck(true);
                message.setUUID(message.getUUID());
                FairLossLink.send(socket, message, message.getSenderHostname(), message.getSenderPort());
                return message.getContent();
            }
        }
    }

    public static void setSource(String address, int port) throws UnknownHostException {
        _address = InetAddress.getByName(address);
        _port = port;
    }


}
