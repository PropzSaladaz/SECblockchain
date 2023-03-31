package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Config.operations.CheckBalanceOperation;
import pt.tecnico.blockchain.Config.operations.ClientOperation;
import pt.tecnico.blockchain.Config.operations.CreateAccountOperation;
import pt.tecnico.blockchain.Config.operations.TransferOperation;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;

import java.security.PublicKey;

public class RequestScheduler {
    private static int slot = 0;
    private static TESClientAPI client;

    public static void setClient(TESClientAPI tes) {
        client = tes;
    }

    public static void startFromConfig(int pid, BlockchainConfig config){
        int slotDuration = config.getSlotDuration();
        ScheduledTask sendertask = new ScheduledTask(() -> {
            ClientOperation request = config.getRequestInSlotForProcess(slot, pid);
            if(request != null){
                parseRequest(request);
            }
            slot++;
        }, slotDuration);
        sendertask.start();
    }

    private static void parseRequest(ClientOperation request) {
        try {
            switch(request.getType()) {
                case BlockchainConfig.CREATE_ACCOUNT:
                    CreateAccountOperation createAcc = (CreateAccountOperation) request;
                    client.createAccount(createAcc.getGasPrice(), createAcc.getGasLimit());
                    break;
                case BlockchainConfig.CHECK_BALANCE:
                    CheckBalanceOperation checkBalance = (CheckBalanceOperation) request;
                    client.checkBalance(checkBalance.getGasPrice(), checkBalance.getGasLimit());
                    break;
                case BlockchainConfig.TRANSFER:
                    TransferOperation transfer = (TransferOperation) request;
                    PublicKey destination = RSAKeyStoreById.getPublicKey(transfer.getDestinationID());
                    if (destination != null) {
                        client.transfer(destination, transfer.getAmount(),
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

}
