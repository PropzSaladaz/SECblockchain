package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.links.APLReturnMessage;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;

import java.io.IOException;
import java.net.DatagramSocket;

public class RunMember {

    public static void run(DatagramSocket socket) throws IOException {

        try {
            while (true) {
                APLReturnMessage message =  AuthenticatedPerfectLink.deliver(socket);
                Thread worker = new Thread(() -> MemberServicesImpl.handleRequest(message));
                worker.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

