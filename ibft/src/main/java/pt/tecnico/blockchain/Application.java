package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;

import java.util.List;

public interface Application {

    void decide(Content msg);

    /**
     * Issued upon having a quorum of signatures of the consensus value. This validation is stronger
     * since it comes with the quorum of signatures of the value to be delivered to the application.
     * Application can then use the quorum to perform stronger validation
     */
    boolean validateCommitValue(Content value, List<Content> quorum);

    /**
     * Issued to validate a value when it is received from the leader.
     * Functions as an optimization to avoid starting instances and only validate the value upon commit
     */
    boolean validatePrePrepareValue(Content value);

    int getNextInstanceNumber();

    void prepareValue(Content value);
}
