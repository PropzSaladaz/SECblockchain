package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;
import pt.tecnico.blockchain.Keys.logger.Logger;
import pt.tecnico.blockchain.client.BlockchainClientAPI;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;

public class RequestScheduler {
    private static int slot = 0;

    public static void startFromConfig(int pid, BlockchainConfig config){
        int slotDuration = config.getSlotDuration();
        ScheduledTask sendertask = new ScheduledTask(() -> {
            Pair<String, Integer> request = config.getRequestInSlotForProcess(slot, pid);
            if(request != null){
                // TODO get type of transaction (CreateAcc, Transfer, Balance)
                TESClientAPI.createAccount(1, 1);
            }
            slot++;
        }, slotDuration);
        sendertask.start();
    }


}
