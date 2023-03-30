package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;

import java.io.IOException;
import java.net.DatagramSocket;

public class RunMember {

    public static void run(DatagramSocket socket, int slotDuration) throws IOException {

        try {
            while (true) {
                Content message =  AuthenticatedPerfectLink.deliver(socket);
                if (message != null) {
                    Thread worker = new Thread(() -> {
                        Content workerMessage = message;
                        MemberServicesImpl.handleRequest(workerMessage);
                    });
                    worker.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

