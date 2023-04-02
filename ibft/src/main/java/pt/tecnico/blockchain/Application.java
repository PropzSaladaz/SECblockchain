package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;

import java.security.NoSuchAlgorithmException;

public interface Application {
    void decide(Pair<BlockchainBlock,BlockchainBlock> pair, Content message);
    boolean validateValue(Content value);
    int getNextInstanceNumber();
    void prepareValue(Content value);
    Content validateTransactions(Content content) throws NoSuchAlgorithmException;
    void setMiner(Boolean isMiner);
}
