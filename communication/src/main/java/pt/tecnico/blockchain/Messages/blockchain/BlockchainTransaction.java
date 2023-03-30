package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

import java.util.UUID;

/**
 * Contains an arbitrary transaction for a specific contract identified by ID
 */
public class BlockchainTransaction extends ApplicationMessage implements Content {

    private String contractID;
    private UUID id;
    private int gasPrice;
    private int gasLimit;

    public BlockchainTransaction(UUID id, Content transaction, int gasPrice, int gasLimit, String contractID) {
        super(transaction);
        this.id = id;
        this.contractID = contractID;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
    }

    @Override
    public String getApplicationMessageType() {
        return null;
        /*TODO: READ / WRITE should be different types -> distinguish from content received */
    }

    public String getContractID() {
        return contractID;
    }

    public String getTransactionID() {
        return id.toString();
    }

    public int getGasPrice() {
        return gasPrice;
    }

    @Override
    public String toString(int tabs) {
        return toStringWithTabs("BlockchainTransaction: {", tabs) +
                toStringWithTabs("contractID: " + contractID, tabs+1) +
                toStringWithTabs("UUID: " + id, tabs+1) +
                getContent().toString(tabs+1) +
                toStringWithTabs("}", tabs);
    }

    @Override
    public boolean equals(Content another) {
        BlockchainTransaction msg = (BlockchainTransaction) another;
        return this.contractID.equals(msg.getContractID())
                && getContent().equals(msg.getContent());
    }
}
