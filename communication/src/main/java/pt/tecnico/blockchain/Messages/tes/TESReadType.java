package pt.tecnico.blockchain.Messages.tes;

public enum TESReadType {
    WEAK(0),
    STRONG(1);

    private final int opID;

    TESReadType(int opID) {
        this.opID = opID;
    }
    public int getCode() {
        return opID;
    }
}
