package pt.tecnico.blockchain.behavior.states.correct;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.APLReturnMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

public abstract class LinkBehavior {

    public void APLsend(DatagramSocket socket, Content content, String hostname, int port) {
        DefaultAPLBehavior.send(socket, content, hostname, port);
    }

    public APLReturnMessage APLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        return DefaultAPLBehavior.deliver(socket);
    }

    public void PLsend(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        DefaultPLBehavior.send(socket, content, hostname, port);
    }

    public Content PLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        return DefaultPLBehavior.deliver(socket);
    }


    public void FLLsend(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        DefaultFLLBehavior.send(socket, content, hostname, port);
    }

    public Content FLLdeliver(DatagramSocket socket) throws IOException, ClassNotFoundException {
        return DefaultFLLBehavior.deliver(socket);
    }

    public abstract String TYPE();

}
