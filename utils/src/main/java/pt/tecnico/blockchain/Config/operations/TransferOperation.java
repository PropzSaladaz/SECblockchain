package pt.tecnico.blockchain.Config.operations;

import pt.tecnico.blockchain.Config.BlockchainConfig;

import java.security.PublicKey;

public class TransferOperation extends ClientOperation {
    private int destination;
    private int amount;


    public TransferOperation(int destinationID, int amount) {
        super(BlockchainConfig.TRANSFER);
        this.destination = destinationID;
        this.amount = amount;
    }

    public TransferOperation(int destinationID, int amount, int gasPrice, int gasLimit) {
        super(BlockchainConfig.TRANSFER, gasPrice, gasLimit);
        this.destination = destinationID;
        this.amount = amount;
    }

    public int getDestinationID() {
        return destination;
    }

    public int getAmount() {
        return amount;
    }
}
