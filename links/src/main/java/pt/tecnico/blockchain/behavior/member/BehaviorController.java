package pt.tecnico.blockchain.behavior.member;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.behavior.member.states.correct.Behavior;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class BehaviorController {
    private static Behavior behavior;

    public static void changeState(Behavior state) {
        behavior = state;
    }

    public static void PLsend(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        behavior.PLsend(socket, content, hostname, port);
    }

    public static Content PLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        return behavior.PLdeliver(socket);
    }

    public static void FLLsend(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        behavior.FLLsend(socket, content, hostname, port);
    }

    public static Content FLLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        return behavior.FLLdeliver(socket);
    }

    public static void setSource(String address, int port) throws UnknownHostException {
        Behavior.setSource(address, port);
    }
}
