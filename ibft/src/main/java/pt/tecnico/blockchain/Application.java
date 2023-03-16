package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;

public interface Application {
    void decide(Content value);
    boolean validateValue(Content value);
    int getNextInstanceNumber();
    void prepareValue(Content value);
}
