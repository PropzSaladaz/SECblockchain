package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Config.operations.CheckBalanceOperation;
import pt.tecnico.blockchain.Config.operations.ClientOperation;
import pt.tecnico.blockchain.Config.operations.CreateAccountOperation;
import pt.tecnico.blockchain.Config.operations.TransferOperation;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.tes.TESReadType;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;
import pt.tecnico.blockchain.contracts.tes.TESClientAPI;

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
                    issueCreateAccRequest((CreateAccountOperation) request);
                    break;
                case BlockchainConfig.CHECK_BALANCE:
                    issueCheckBalanceRequest((CheckBalanceOperation) request);
                    break;
                case BlockchainConfig.TRANSFER:
                    issueTransferRequest((TransferOperation) request);
                    break;
            }
        } catch( ClassCastException e) {
            Logger.logWarning("Invalid client operation");
        }
    }

    private static void issueCreateAccRequest(CreateAccountOperation request) {
        client.createAccount(request.getGasPrice(), request.getGasLimit());
    }

    private static void issueCheckBalanceRequest(CheckBalanceOperation request) {
        switch (request.getReadType()) {
            case BlockchainConfig.WEAKLY_CONSISTENT_READ:
                client.checkBalance(TESReadType.WEAK, request.getGasPrice(), request.getGasLimit());
                break;
            case BlockchainConfig.STRONGLY_CONSISTENT_READ:
                client.checkBalance(TESReadType.STRONG, request.getGasPrice(), request.getGasLimit());
                break;
            default:
                break;
        }
    }

    private static void issueTransferRequest(TransferOperation request) {
        PublicKey destination = RSAKeyStoreById.getPublicKey(request.getDestinationID());
        if (destination != null) client.transfer(destination, request.getAmount(),
                request.getGasPrice(), request.getGasLimit());
        else Logger.logError("Client with ID=" + request.getDestinationID() + " does not have a valid key");
    }

}
