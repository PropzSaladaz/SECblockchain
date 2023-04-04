package pt.tecnico.blockchain.client;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionStatus;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionType;

public interface DecentralizedAppClientAPI {
    void deliver(Content message, BlockchainTransactionType type, BlockchainTransactionStatus status);
}
