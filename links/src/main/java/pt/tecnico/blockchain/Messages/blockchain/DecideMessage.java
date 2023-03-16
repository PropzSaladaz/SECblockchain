package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class DecideMessage extends ApplicationMessage implements Content {

    private int _round;
    private List<Integer> _quorum;

    public DecideMessage(int round, List<Integer> quorum, Content content) {
        super(content);
        _round = round;
        _quorum = quorum;
    }

    @Override
    public String getApplicationMessageType() {
        return DECIDE_BLOCK_MESSAGE;
    }

    public int getRound() {
        return _round;
    }

    public List<Integer> getQuorum() {
        return _quorum;
    }

    @Override
    public String toString(int level) {
        return toStringWithTabs("DecideMessage: {", level) +
                toStringWithTabs("round: " + _round, level+1) +
                toStringWithTabs("Quorum: " + _quorum.toString(), level+1) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }
}
