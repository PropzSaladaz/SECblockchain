package pt.tecnico.blockchain.Messages.blockchain;

public enum BlockchainTransactionStatus {
    VALIDATED("VALIDATED"),
    REJECTED("REJECTED"),
    NOT_VALIDATED_YET("NOT VALIDATED YET");
    private final String opID;

    BlockchainTransactionStatus(String opID) {
        this.opID = opID;
    }
    public String getMessage() {
        return opID;
    }
}
