package pt.tecnico.blockchain.Messages.tes;

public enum TESReadType {
    WEAK("WEAK"),
    STRONG("STRONG");

    private final String opID;

    TESReadType(String opID) {
        this.opID = opID;
    }
    public String getCode() {
        return opID;
    }
}
