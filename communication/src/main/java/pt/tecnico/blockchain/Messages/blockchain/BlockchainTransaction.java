package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

import java.util.UUID;

/**
 * Contains an arbitrary transaction for a specific contract identified by ID
 */
public class BlockchainTransaction extends ApplicationMessage implements Content {
    public static final String APPENDED = "APPENDED";
    public static final String STRONG_READ = "STRONG READ";
    public static final String WEAK_READ = "WEAK READ";
    public static final String UPDATE = "UPDATE";
    public static final String READ = "READ";


    private String contractID;
    private String from;
    private int nonce;
    private int gasPrice;
    private int gasLimit;
    private String _status;
    private String _operationType;


    public BlockchainTransaction(String from, int nonce, Content transaction, int gasPrice, int gasLimit, String contractID) {
        super(transaction);
        this.from = from;
        this.nonce = nonce;
        this.contractID = contractID;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        _status = "NOT APPENDED";
    }

    @Override
    public String getApplicationMessageType() {
        return ApplicationMessage.BLOCKCHAIN_TRANSACTION_MESSAGE;
    }

    public String getContractID() {
        return contractID;
    }

    public Integer getNonce() {
        return nonce;
    }

    public String getStatus() {
        return _status;
    }

    public void setOperationType(String operationType) {
        _operationType = operationType;
    }

    public String getOperationType() {
        return _operationType;
    }

    public void setStatus(String status) {
        _status = status;
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
