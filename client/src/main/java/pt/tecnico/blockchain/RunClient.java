package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.AppendBlockMessage;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.links.APLMessage;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;

public class RunClient {
    public static void run(DatagramSocket socket,int pid,BlockchainConfig config){
        int[] slotCounter = {1};
        int slotDuration = config.getSlotDuration();
        Thread worker = new Thread(() -> {
            try {
                while (true){
                    APLMessage message = (APLMessage) AuthenticatedPerfectLink.deliver(socket);
                    ClientServiceImpl.handleRequest((ApplicationMessage) message.getContent());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        worker.start();

        ScheduledTask sendertask = new ScheduledTask(() -> {
            Pair<String, Integer> request = config.getRequestInSlotForProcess(slotCounter[0], pid);
            if(request != null){
                String message = request.getFirst();
                Content appendMessage = new AppendBlockMessage(new BlockchainMessage(message));
                try {
                    System.out.println("Sending Block " + message + "\n");
                    ClientFrontend.broadcastClientRequests(appendMessage);
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            slotCounter[0]++;
        }, slotDuration);

        sendertask.start();


    }


}
