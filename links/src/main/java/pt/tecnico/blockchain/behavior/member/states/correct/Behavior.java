package pt.tecnico.blockchain.behavior.member.states.correct;

import pt.tecnico.blockchain.Messages.Content;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

public abstract class Behavior {

    public void APLsend(DatagramSocket socket, Content content, String hostname, int port) {
        DefaultAPLBehavior.send(socket, content, hostname, port);
    }

    public Content APLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        System.out.println("default APLdeliver");
        return DefaultAPLBehavior.deliver(socket);
    }

    public void PLsend(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        DefaultPLBehavior.send(socket, content, hostname, port);
    }

    public Content PLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        System.out.println("default PLdeliver");
        return DefaultPLBehavior.deliver(socket);
    }


    public void FLLsend(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        DefaultFLLBehavior.send(socket, content, hostname, port);
    }

    public Content FLLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        System.out.println("default FLLdeliver");
        return DefaultFLLBehavior.deliver(socket);
    }

    public abstract String TYPE();

}
