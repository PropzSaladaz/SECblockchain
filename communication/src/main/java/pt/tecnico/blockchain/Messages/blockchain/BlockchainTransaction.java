package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

import java.util.UUID;

/**
 * Contains an arbitrary transaction for a specific contract identified by ID
 */
public class BlockchainTransaction extends ApplicationMessage implements Content {
    public static final String SUCCESSFUL_TRANSACTION = "SUCCESSFUL TRANSACTION";
    public static final String REJECTED_TRANSACTION = "REJECTED TRANSACTION";
    public static final String NOT_EXECUTED = "REJECTED TRANSACTION";

    private String contractID;
    private String from;
    private int nonce;
    private int gasPrice;
    private int gasLimit;
    private String status = NOT_EXECUTED;

    public BlockchainTransaction(String from, int nonce, Content transaction, int gasPrice, int gasLimit, String contractID) {
        super(transaction);
        this.from = from;
        this.nonce = nonce;
        this.contractID = contractID;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
    }

    @Override
    public String getApplicationMessageType() {
        return ApplicationMessage.BLOCKCHAIN_TRANSACTION_MESSAGE;
    }

    public String getContractID() {
        return contractID;
    }

    public String getNonce() {
        return Integer.toString(nonce);
    }

    public int getGasPrice() {
        return gasPrice;
    }

    public String getSender() {
        return from;
    }

    public Pair<String, Integer> getTransactionID() {
        return new Pair<>(from, nonce);
    }

    @Override
    public String toString(int tabs) {
        return toStringWithTabs("BlockchainTransaction: {", tabs) +
                toStringWithTabs("contractID: " + contractID, tabs+1) +
                toStringWithTabs("nonce: " + nonce, tabs+1) +
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
