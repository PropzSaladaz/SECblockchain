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

    public static void run(DatagramSocket socket,int pid,BlockchainConfig config){
        int slotDuration = config.getSlotDuration();
        Thread worker = new Thread(() -> {
            try {
                while (true){
                    ApplicationMessage message = (ApplicationMessage) AuthenticatedPerfectLink.deliver(socket);
                    ClientServiceImpl.handleRequest(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        worker.start();

        ScheduledTask sendertask = new ScheduledTask(() -> {
            Pair<String, Integer> request = config.getRequestInSlotForProcess(slot, pid);
            if(request != null){
                String message = request.getFirst();
                Content appendMessage = new AppendBlockMessage(new BlockchainMessage(message));
                try {
                    Logger.logWithTime("Sending Block " + message + "\n");
                    ClientFrontend.broadcastClientRequests(appendMessage);
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            slot++;
        }, slotDuration);

        sendertask.start();


    }


}
