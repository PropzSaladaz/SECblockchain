package pt.tecnico.blockchain.Messages.blockchain;

public enum BlockchainTransactionType {
    READ("READ"),
    UPDATE("UPDATE");

    private final String opID;

    BlockchainTransactionType(String opID) {
        this.opID = opID;
    }
    public String getMessage() {
        return opID;
    }
}
