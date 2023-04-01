package pt.tecnico.blockchain.Config.operations;

import pt.tecnico.blockchain.Config.BlockchainConfig;

public class CreateAccountOperation extends ClientOperation {
    public CreateAccountOperation() {
        super(BlockchainConfig.CREATE_ACCOUNT);
    }
}
