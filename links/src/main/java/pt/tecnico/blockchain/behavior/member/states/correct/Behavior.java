package pt.tecnico.blockchain.behavior.member.states.correct;

import pt.tecnico.blockchain.FairLossLink;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.links.FLLMessage;
import pt.tecnico.blockchain.Messages.links.PLMessage;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;
import pt.tecnico.blockchain.Timeout;
import pt.tecnico.blockchain.UuidGenerator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static pt.tecnico.blockchain.PerfectLink.ASSUME_FAILURE_TIMEOUT;
import static pt.tecnico.blockchain.PerfectLink.RESEND_MESSAGE_TIMEOUT;

public abstract class Behavior {
    private static final ConcurrentHashMap<UUID, PLMessage> _ackMessages = new ConcurrentHashMap<>();
    private static InetAddress _address;
    private static int _port;

    private static String _source;
    private static RSAKeyStoreById _store;
    private static int _id;


    public boolean hasAckArrived(UUID seqNum){
        return _ackMessages.containsKey(seqNum);
    }

    public void PLsend(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        PLMessage message = new PLMessage(_address, _port,  content);
        message.setUUID(UuidGenerator.generateUuid());
        message.setAck(false);
        System.out.println("Sending to: " + port);
        ScheduledTask task = new ScheduledTask( () ->
                FairLossLink.send(socket, message, hostname , port), RESEND_MESSAGE_TIMEOUT);
        Timeout timeoutTask = new Timeout( () -> {
            task.start();
            while (!hasAckArrived(message.getUUID())); // TODO Active waiting
            task.stop();
            System.out.println("PL - Ack received");
        }, ASSUME_FAILURE_TIMEOUT);
        timeoutTask.addInternalScheduleTask(task);
        timeoutTask.run();
        System.out.println("Finished sending, ready to send next message!");
    }


    public Content PLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        while(true){
            PLMessage message = (PLMessage) FairLossLink.deliver(socket);
            System.out.println("PL - message received");
            if (message.isAck()) {
                System.out.println("PL - message received and is ACK");
                _ackMessages.put(message.getUUID(),message);
            }else if (!hasAckArrived(message.getUUID())) {
                message.setAck(true);
                message.setUUID(message.getUUID());
                FairLossLink.send(socket, message, message.getSenderHostname(), message.getSenderPort());
                return message.getContent();
            }
        }
    }



    public void FLLsend(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        try {
            FLLMessage message = new FLLMessage(content);
            System.out.println("Sending FLL message: \n" + message.toString());
            socket.send(MessageManager.createPacket(message, hostname, port));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Content FLLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        System.out.println("Waiting for FLL messages...");
        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        FLLMessage message = MessageManager.createMessage(packet.getData());
        System.out.println("FLL message received: \n" + message.toString());
        return message.getContent();
    }

    public static void setSource(String address, int port) throws UnknownHostException {
        _address = InetAddress.getByName(address);
        _port = port;
    }


}
