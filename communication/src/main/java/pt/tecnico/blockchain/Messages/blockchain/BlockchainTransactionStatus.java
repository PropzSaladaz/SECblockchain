package pt.tecnico.blockchain.Messages.blockchain;

public enum BlockchainTransactionStatus {
    SUCCESS(0),
    FAILURE(1),
    APPENDED(2);

    private final int opID;

    BlockchainTransactionStatus(int opID) {
        this.opID = opID;
    }
    public int getCode() {
        return opID;
    }
}
