package pt.tecnico.blockchain;

import pt.tecnico.blockchain.SlotTimer.ScheduledTask;
import pt.tecnico.blockchain.Messages.Content;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;

public class RunMember {

    public static void run(DatagramSocket socket, int slotDuration) throws IOException {

        // We need to re-call deliver method on every slot otherwise the deliver method will not have
        // the behavior we want for the current slot, and will maintain the last behavior with which it was called
        Timeout t = new Timeout(() -> {
            try {
                Content message = AuthenticatedPerfectLink.deliver(socket);
                System.out.println("Before sending");
                MemberServicesImpl.handleRequest(message);
                System.out.println("After sending");
            } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }, slotDuration);

        ScheduledTask st = new ScheduledTask(t::run, slotDuration);
        st.start();
    }
}

