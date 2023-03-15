package pt.tecnico.blockchain.behavior.member.states.corrupt;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.behavior.member.states.correct.Behavior;
import pt.tecnico.blockchain.behavior.member.states.corrupt.CorruptAPLBehavior;
import pt.tecnico.blockchain.behavior.member.states.correct.DefaultAPLBehavior;
import pt.tecnico.blockchain.behavior.member.states.correct.DefaultFLLBehavior;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

/**
 * Act arbitrary in several layers to corrupt messages
 */
public class ArbitraryState extends Behavior {
    private double prob = 0.6;

    private boolean actArbitrary() {
        return Math.random() < prob;
    }

    @Override
    public void APLsend(DatagramSocket socket, Content content, String hostname, int port) {
        System.out.println("Corrupted APLsend");
        if (actArbitrary()) CorruptAPLBehavior.send(socket, content, hostname, port);
        else DefaultAPLBehavior.send(socket, content, hostname, port);
    }

    @Override
    public Content APLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        System.out.println("Corrupted APLdeliver");
        if (actArbitrary()) return CorruptAPLBehavior.deliver(socket);
        else return DefaultAPLBehavior.deliver(socket);
    }

    @Override
    public void FLLsend(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        System.out.println("Corrupted FLLsend");
        if (actArbitrary()) CorruptFLLBehavior.send(socket, content, hostname, port);
        else DefaultFLLBehavior.send(socket, content, hostname, port);
    }

    @Override
    public String TYPE() {
        return "Corrupt";
    }
}
