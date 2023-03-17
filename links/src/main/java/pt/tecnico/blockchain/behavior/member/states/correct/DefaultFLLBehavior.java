package pt.tecnico.blockchain.behavior.member.states.correct;

import pt.tecnico.blockchain.Keys.logger.Logger;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.links.FLLMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DefaultFLLBehavior {

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        try {
            FLLMessage message = new FLLMessage(content);
            Logger.logWithTime("\n\033[36m\033[1mSending message: \033[0m\n" + message.toString());
            socket.send(MessageManager.createPacket(message, hostname, port));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        FLLMessage message = MessageManager.createMessage(packet.getData());
        Logger.logWithTime("\n\033[32m\033[1mMessage received: \033[0m\n" + message.toString());
        return message.getContent();
    }
}
