package pt.tecnico.blockchain.behavior.states.correct;

import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.links.FairLossLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.PLMessage;
import pt.tecnico.blockchain.links.PerfectLink;
import pt.tecnico.blockchain.Pair;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static pt.tecnico.blockchain.links.PerfectLink.RESEND_MESSAGE_TIMEOUT;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;


public class DefaultPLBehavior {

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        PLMessage message = new PLMessage(PerfectLink.getAddress(), PerfectLink.getPort(),  content);
        Pair<InetAddress, Integer> receiverInfo = new Pair<>(hostname, port);
        Integer currentSeqNum = PerfectLink.getAckSeqNum(receiverInfo);
        message.setSeqNum(currentSeqNum);
        message.setAck(false);
        PerfectLink.incrAckSeqNum(receiverInfo);
        ScheduledTask task = new ScheduledTask( () -> FairLossLink.send(socket, message, hostname , port)
        , RESEND_MESSAGE_TIMEOUT);
        PerfectLink.addToStubbornTasks(receiverInfo.toString() + currentSeqNum, task);
        task.start();
    }


    /**
     * Only returns if received a valid PL message.
     * If the message is invalid simply ignore it and wait for a valid one.
     */
    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        while (true) {
            PLMessage message = (PLMessage) FairLossLink.deliver(socket);
            Pair<InetAddress,Integer> sender = new Pair<>(message.getSenderHostname(), message.getSenderPort());
            if (message.isAck()) {
                PerfectLink.stopStubbornTask(sender.toString() + message.getSeqNum());
            }
            else if (!message.isAck() && message.getSeqNum() >= PerfectLink.getDeliveredSeqNum(sender)) {
                PerfectLink.incrDeliveredSeqNum(sender);
                PerfectLink.sendAck(socket, message);
                return message.getContent();
            }
        }
    }
}
