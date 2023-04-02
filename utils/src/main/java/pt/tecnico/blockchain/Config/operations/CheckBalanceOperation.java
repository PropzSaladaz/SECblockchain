package pt.tecnico.blockchain.Config.operations;

import pt.tecnico.blockchain.Config.BlockchainConfig;

public class CheckBalanceOperation extends ClientOperation {
    private String readType;
    public CheckBalanceOperation(String readType) {
        super(BlockchainConfig.CHECK_BALANCE);
        this.readType = readType;
    }

    public String getReadType() {
        return readType;
    }
}
