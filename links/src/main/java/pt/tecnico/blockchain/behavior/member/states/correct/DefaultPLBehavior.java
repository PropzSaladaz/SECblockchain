package pt.tecnico.blockchain.behavior.member.states.correct;

import pt.tecnico.blockchain.FairLossLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.PLMessage;
import pt.tecnico.blockchain.PerfectLink;
import pt.tecnico.blockchain.Pair;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static pt.tecnico.blockchain.PerfectLink.RESEND_MESSAGE_TIMEOUT;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;


public class DefaultPLBehavior {

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        PLMessage message = new PLMessage(PerfectLink.getAddress(), PerfectLink.getPort(),  content);
        Pair<InetAddress, Integer> receiverInfo = new Pair<InetAddress,Integer>(hostname, port);
        Integer currentSeqNum = PerfectLink.getAckSeqNum(receiverInfo);
        message.setSeqNum(currentSeqNum);
        PerfectLink.incrAckSeqNum(receiverInfo);
        message.setAck(false);
        ScheduledTask task = new ScheduledTask( () -> {
            FairLossLink.send(socket, message, hostname , port);
        }, RESEND_MESSAGE_TIMEOUT);
        task.start();
        PerfectLink.addToStubbornTasks(receiverInfo.toString() + Integer.toString(currentSeqNum), task);        
    }


    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        PLMessage message = (PLMessage) FairLossLink.deliver(socket);
        Pair<InetAddress,Integer> sender = new Pair<InetAddress,Integer>(message.getSenderHostname(), message.getSenderPort());
        Integer seqNum = message.getSeqNum();
        if (message.isAck() && seqNum >= PerfectLink.getAckSeqNum(sender)) {
            PerfectLink.stopStubbornTask(sender.toString() + Integer.toString(seqNum-1));
        }
        else if (!message.isAck() && seqNum == PerfectLink.getDeliveredSeqNum(sender)) {
            PerfectLink.incrDeliveredSeqNum(sender);
            PerfectLink.sendAck(socket, message);
            return message.getContent();
        }
        return null;
    }
}
