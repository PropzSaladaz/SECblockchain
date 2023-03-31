package pt.tecnico.blockchain.behavior;

import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.APLReturnMessage;
import pt.tecnico.blockchain.behavior.states.correct.LinkBehavior;
import pt.tecnico.blockchain.behavior.states.correct.CorrectState;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

public class LinkBehaviorController {
    private static LinkBehavior behavior = new CorrectState();

    public static void changeState(LinkBehavior state) {
        behavior = state;
        Logger.logBehavior("changed state to " + behavior.TYPE());
    }

    public static String getBehaviorType() {
        return behavior.TYPE();
    }

    public static void APLsend(DatagramSocket socket, Content content, String hostname, int port) {
        behavior.APLsend(socket, content, hostname, port);
    }

    public static APLReturnMessage APLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        return behavior.APLdeliver(socket);
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
}
