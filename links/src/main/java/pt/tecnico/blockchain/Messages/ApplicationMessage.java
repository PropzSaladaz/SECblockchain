package pt.tecnico.blockchain.Messages;

public abstract class ApplicationMessage extends Message {

    public static final String CONSENSUS_INSTANCE_MESSAGE = "CONSENSUS_INSTANCE_MESSAGE";
    public static final String APPEND_BLOCK_MESSAGE = "APPEND_BLOCK_MESSAGE";
    public static final String DECIDE_BLOCK_MESSAGE = "DECIDE_BLOCK_MESSAGE";
    public static final String DECIDE_BLOCK_CLIENT = "DECIDE_BLOCK_CLIENT";

    public ApplicationMessage(){

    }

    public ApplicationMessage(Content content){
        super(content);
    }

    public abstract String getApplicationMessageType();
}
