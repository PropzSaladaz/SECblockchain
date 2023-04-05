package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;

import java.util.List;

public interface Application {
    void decide(Content msg);
    boolean validateValue(Content value, List<Content> quorum);
    int getNextInstanceNumber();
    void prepareValue(Content value);
}
