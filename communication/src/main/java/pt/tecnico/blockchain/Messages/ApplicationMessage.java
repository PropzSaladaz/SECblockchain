package pt.tecnico.blockchain.Messages;

public abstract class ApplicationMessage extends Message {

    public static final String CONSENSUS_INSTANCE_MESSAGE = "CONSENSUS_INSTANCE_MESSAGE";
    public static final String APPEND_BLOCK_MESSAGE = "APPEND_BLOCK_MESSAGE";
    public static final String DECIDE_BLOCK_MESSAGE = "DECIDE_BLOCK_MESSAGE";
    public static final String BLOCKCHAIN_TRANSACTION_MESSAGE = "BLOCKCHAIN_TRANSACTION_MESSAGE";
    public static final String TRANSACTION_RESULT_MESSAGE = "TRANSACTION_RESULT_MESSAGE";

    public ApplicationMessage(){

    }

    public ApplicationMessage(Content content){
        super(content);
    }

    public abstract String getApplicationMessageType();
}
