package pt.tecnico.blockchain.behavior.member.states.ommit;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.behavior.member.states.correct.Behavior;
import pt.tecnico.blockchain.behavior.member.states.correct.DefaultAPLBehavior;
import pt.tecnico.blockchain.behavior.member.states.correct.DefaultFLLBehavior;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

public class OmissionState extends Behavior {

    public static final String TYPE = "Omit";

    @Override
    public String TYPE() {
        return TYPE;
    }

    @Override
    public void APLsend(DatagramSocket socket, Content content, String hostname, int port) {
        System.out.println("Omission behavior APL send called!");
        OmissionAPLBehavior.send(socket, content, hostname, port);
    }

    @Override
    public Content APLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        return OmissionAPLBehavior.deliver(socket);
    }

    @Override
    public void FLLsend(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        OmissionFLLBehavior.send(socket, content, hostname, port);
    }
}
