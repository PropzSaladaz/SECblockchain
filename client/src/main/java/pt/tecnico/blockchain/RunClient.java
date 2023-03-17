package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.AppendBlockMessage;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;
import pt.tecnico.blockchain.Keys.logger.Logger;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;

public class RunClient {
    private static int slot = 0;

    public static void run(DatagramSocket socket,int pid, BlockchainConfig config){
        int slotDuration = config.getSlotDuration();
        Thread worker = new Thread(() -> {
            while (true){
                try {
                    ClientServiceImpl.handleRequest(AuthenticatedPerfectLink.deliver(socket));
                } catch (ClassCastException | IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                    System.out.println("Received a corrupted message, ignoring...");
                }
            }
        });
        ScheduledTask sendertask = new ScheduledTask(() -> {
            Pair<String, Integer> request = config.getRequestInSlotForProcess(slot, pid);
            Pair<String, Integer> hostname = config.getClientHostname(pid);
            if(request != null){
                String message = request.getFirst();
                Content appendMessage = new AppendBlockMessage(
                    new BlockchainMessage(message, hostname.getFirst(), hostname.getSecond())
                );
                try {
                    Logger.logWithTime("Sending Block " + message + "\n");
                    ClientFrontend.broadcastClientRequests(appendMessage);
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            slot++;
        }, slotDuration);

        worker.start();
        sendertask.start();
    }


}
