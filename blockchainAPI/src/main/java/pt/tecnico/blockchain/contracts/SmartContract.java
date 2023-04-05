package pt.tecnico.blockchain.contracts;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;

public interface SmartContract {
    String getContractID();

    boolean validateAndExecuteTransaction(Content transaction,String minerKey);
    Content getTransactionResponse(Content transaction);

    /**
     * May be used to read current state from contract and set that state to transactions, such as
     * account balance, or something else before the block starts to get proposed
     */
    void updateTransactionWithCurrentState(Content transaction);

}