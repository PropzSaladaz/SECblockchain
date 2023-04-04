package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;

import java.util.List;

public interface Application {
    void decide(Content msg, List<Content> quorum);
    boolean validateValue(Content value);
    int getNextInstanceNumber();
    void prepareValue(Content value);
}
