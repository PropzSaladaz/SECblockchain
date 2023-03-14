package pt.tecnico.blockchain;

import java.net.*;
import java.io.*;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.behavior.member.BehaviorController;

public class PerfectLink {
    public static final int ASSUME_FAILURE_TIMEOUT = 5000;
    public static final int RESEND_MESSAGE_TIMEOUT = 2000;

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        BehaviorController.PLsend(socket, content, hostname, port);
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        return BehaviorController.PLdeliver(socket);
    }

    public static void setSource(String address, int port) throws UnknownHostException {
        BehaviorController.setSource(address, port);
    }

}
