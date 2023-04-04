package pt.tecnico.blockchain.Messages.blockchain;

public enum BlockchainTransactionType {
    READ(0),
    UPDATE(1);

    private final int opID;

    BlockchainTransactionType(int opID) {
        this.opID = opID;
    }
    public int getCode() {
        return opID;
    }
}
