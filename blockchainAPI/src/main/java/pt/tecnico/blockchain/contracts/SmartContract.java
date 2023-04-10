package pt.tecnico.blockchain.contracts;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;

public interface SmartContract {
    String getContractID();


    /**
     * Will be used by the member that initially proposes the block to set the status of all transactions
     * before proposing the block
     */
    boolean validateTransaction(Content transaction, Object tempState);

    Object getNewValidationState();

    /**
     * Will be used by every member upon receiving a consensus-proposed block
     */
    boolean validateAndExecuteTransaction(Content transaction, String minerKey, Content transactionsProof);

    Content getTransactionResponse(Content transaction, BlockchainTransactionStatus status, String memberPubKey);

    /**
     * May be used to read current state from contract and set that state to transactions, such as
     * account balance, or something else before the block starts to get proposed
     */
    void updateTransactionWithCurrentState(Content transaction);

}