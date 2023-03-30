package pt.tecnico.blockchain.Config.operations;

import pt.tecnico.blockchain.Config.BlockchainConfig;

public class CheckBalanceOperation extends ClientOperation{
    public CheckBalanceOperation(int gasPrice, int gasLimit) {
        super(BlockchainConfig.CHECK_BALANCE, gasPrice, gasLimit);
    }
}
