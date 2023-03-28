package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;

import java.io.IOException;
import java.net.DatagramSocket;

public class RunMember {

    public static void run(DatagramSocket socket, int slotDuration) throws IOException {

        Thread worker = new Thread(() -> {
            try {
                while (true) {
                    Content message =  AuthenticatedPerfectLink.deliver(socket);
                    if (message != null) MemberServicesImpl.handleRequest(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        worker.start();
    }
}

