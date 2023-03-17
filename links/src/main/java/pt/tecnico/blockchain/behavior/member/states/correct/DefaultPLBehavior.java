package pt.tecnico.blockchain.behavior.member.states.correct;

import pt.tecnico.blockchain.FairLossLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.PLMessage;
import pt.tecnico.blockchain.PerfectLink;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;
import pt.tecnico.blockchain.Timeout;
import pt.tecnico.blockchain.UuidGenerator;
import pt.tecnico.blockchain.behavior.member.BehaviorController;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static pt.tecnico.blockchain.PerfectLink.RESEND_MESSAGE_TIMEOUT;

public class DefaultPLBehavior {

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        PLMessage message = new PLMessage(PerfectLink.getAddress(), PerfectLink.getPort(),  content);
        message.setUUID(UuidGenerator.generateUuid());
        message.setAck(false);
        ScheduledTask task = new ScheduledTask( () -> {
            System.out.println("Sending to: " + port);
            System.out.println("Current state is " + BehaviorController.getBehaviorType());
            FairLossLink.send(socket, message, hostname , port);
        }, RESEND_MESSAGE_TIMEOUT);
        task.setStopCondition(() -> PerfectLink.hasAckArrived(message.getUUID()));
        task.start();
    }


    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        while(true){
            PLMessage message = (PLMessage) FairLossLink.deliver(socket);
            System.out.println("PL - message received");
            if (message.isAck()) {
                System.out.println("PL - message received and is ACK");
                PerfectLink.putACK(message.getUUID(),message);
            }else if (!PerfectLink.hasAckArrived(message.getUUID())) {
                PerfectLink.sendAck(socket, message);
                return message.getContent();
            }
        }
    }
}
