package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

import java.util.List;

public interface Application {
    void decide(Content message); // TODO maybe use an object instead of arrayList
    boolean validateValue(Content value);
    int getNextInstanceNumber();
    void prepareValue(Content value);
}
