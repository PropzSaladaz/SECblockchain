package pt.tecnico.blockchain.behavior.member.states.signas;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.behavior.member.states.correct.Behavior;

import java.net.DatagramSocket;

public class SignAsState extends Behavior {
    private int signAs;

    public SignAsState(int id) {
        signAs = id;
    }

    public int getId() {
        return signAs;
    }

    @Override
    public void APLsend(DatagramSocket socket, Content content, String hostname, int port) {
        SignAsAPLBehavior.send(socket, content, hostname, port, signAs);
    }

    @Override
    public String TYPE() {
        return "SignAs";
    }
}
