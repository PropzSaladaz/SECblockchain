package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;

import java.util.ArrayList;
import java.util.List;

public interface Application {
    void decide(Content value, List<Integer> quorum); // TODO maybe use an object instead of arrayList
    boolean validateValue(Content value);
    int getNextInstanceNumber();
    void prepareValue(Content value);
}
