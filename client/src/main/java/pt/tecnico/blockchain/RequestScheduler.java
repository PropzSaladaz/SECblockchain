package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Config.operations.CheckBalanceOperation;
import pt.tecnico.blockchain.Config.operations.ClientOperation;
import pt.tecnico.blockchain.Config.operations.CreateAccountOperation;
import pt.tecnico.blockchain.Config.operations.TransferOperation;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;
import pt.tecnico.blockchain.client.BlockchainClientAPI;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class RequestScheduler {
    private static int slot = 0;

    public static void startFromConfig(int pid, BlockchainConfig config){
        int slotDuration = config.getSlotDuration();
        ScheduledTask sendertask = new ScheduledTask(() -> {
            ClientOperation request = config.getRequestInSlotForProcess(slot, pid);
            if(request != null){
                try {
                    switch(request.getType()) {
                        case BlockchainConfig.CREATE_ACCOUNT:
                            CreateAccountOperation createAcc = (CreateAccountOperation) request;
                            TESClientAPI.createAccount(createAcc.getGasPrice(), createAcc.getGasLimit());
                            break;
                        case BlockchainConfig.CHECK_BALANCE:
                            CheckBalanceOperation checkBalance = (CheckBalanceOperation) request;
                            TESClientAPI.checkBalance(checkBalance.getGasPrice(), checkBalance.getGasLimit());
                            break;
                        case BlockchainConfig.TRANSFER:
                            TransferOperation transfer = (TransferOperation) request;
                            PublicKey destination = RSAKeyStoreById.getPublicKey(transfer.getDestinationID());
                            if (destination != null) {
                                TESClientAPI.transfer(destination, transfer.getAmount(),
                                        transfer.getGasPrice(), transfer.getGasLimit());
                            } else {
                                Logger.logWarning("Client with ID=" + transfer.getDestinationID() +
                                        " does not have a valid key");
                            }
                            break;
                    }
                } catch( ClassCastException e) {
                    Logger.logWarning("Invalid client operation");
                }
            }
            slot++;
        }, slotDuration);
        sendertask.start();
    }


}
