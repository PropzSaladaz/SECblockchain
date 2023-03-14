package pt.tecnico.blockchain;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.io.*;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.links.FLLMessage;
import pt.tecnico.blockchain.behavior.member.BehaviorController;

public class FairLossLink {


    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        BehaviorController.FLLsend(socket, content, hostname, port);
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        return BehaviorController.FLLdeliver(socket);
    }


}