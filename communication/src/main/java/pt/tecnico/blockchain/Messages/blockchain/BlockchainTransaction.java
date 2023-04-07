package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

import java.util.UUID;

/**
 * Contains an arbitrary transaction for a specific contract identified by ID
 */
public class BlockchainTransaction extends ApplicationMessage implements Content {

    private String contractID;
    private String from;
    private int nonce;
    private int gasPrice;
    private int gasLimit;
    private BlockchainTransactionStatus _status;
    private BlockchainTransactionType _operationType;
    private String failureMessage;


    public BlockchainTransaction(String from, int nonce, Content transaction, int gasPrice, int gasLimit, String contractID) {
        super(transaction);
        this.from = from;
        this.nonce = nonce;
        this.contractID = contractID;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        _status = BlockchainTransactionStatus.NOT_VALIDATED_YET; // TODO change to unspecified
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

    public BlockchainTransactionStatus getStatus() {
        return _status;
    }

    public void setOperationType(BlockchainTransactionType operationType) {
        _operationType = operationType;
    }

    public BlockchainTransactionType getOperationType() {
        return _operationType;
    }

    public void setStatus(BlockchainTransactionStatus status) {
        _status = status;
    }

    public void setStatus(BlockchainTransactionStatus status, String message) {
        _status = status;
        failureMessage = message;
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
                toStringWithTabs("from: " + from.substring(0, 15), tabs+1) +
                toStringWithTabs("status: " + _status.getMessage(), tabs+1) +
                toStringWithTabs("operationType: " + _operationType.getMessage(), tabs+1) +
                toStringWithTabs("gasPrice: " + gasPrice, tabs+1) +
                toStringWithTabs("gasLimit: " + gasLimit, tabs+1) +
                toStringWithTabs("failureMessage: " + failureMessage, tabs+1) +
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
