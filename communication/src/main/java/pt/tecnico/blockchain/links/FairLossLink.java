package pt.tecnico.blockchain.links;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.io.*;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.behavior.LinkBehaviorController;

public class FairLossLink {


    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        LinkBehaviorController.FLLsend(socket, content, hostname, port);
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        return LinkBehaviorController.FLLdeliver(socket);
    }


}