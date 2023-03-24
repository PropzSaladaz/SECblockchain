package pt.tecnico.blockchain.behavior.member.states.correct;

import pt.tecnico.blockchain.FairLossLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.PLMessage;
import pt.tecnico.blockchain.PerfectLink;
import pt.tecnico.blockchain.Pair;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class DefaultPLBehavior {

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        PLMessage message = new PLMessage(PerfectLink.getAddress(), PerfectLink.getPort(),  content);
        Pair<InetAddress, Integer> senderInfo = new Pair<InetAddress,Integer>(hostname, port);
        message.setSeqNum(PerfectLink.getAckSeqNum(senderInfo));
        message.setAck(false);
        PerfectLink.incrAckSeqNum(senderInfo);
        FairLossLink.send(socket, message, hostname , port);
    }


    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        PLMessage message = (PLMessage) FairLossLink.deliver(socket);
        Pair<InetAddress,Integer> sender = new Pair<InetAddress,Integer>(message.getSenderHostname(), message.getSenderPort());
        if (message.isAck() && message.getSeqNum() == PerfectLink.getAckSeqNum(sender)) {
            PerfectLink.incrAckSeqNum(sender);
        }
        else if (!message.isAck() && message.getSeqNum() == PerfectLink.getDeliveredSeqNum(sender)) {
            PerfectLink.incrDeliveredSeqNum(sender);
            PerfectLink.sendAck(socket, message);
            return message.getContent();
        }
        return null;
    }
}
