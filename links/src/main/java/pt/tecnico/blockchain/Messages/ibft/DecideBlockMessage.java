package pt.tecnico.blockchain.Messages.ibft;

import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class DecideBlockMessage extends ApplicationMessage implements Content {

    private int _round;
    private List<Integer> _quorum;
    private String _hash;

    public DecideBlockMessage(int round, List<Integer> quorum, Content content,String previousBlockHash) throws NoSuchAlgorithmException {
        super(content);
        _round = round;
        _quorum = quorum;
        BlockchainMessage message = (BlockchainMessage) content;
        _hash = Crypto.computeHash(message.getMessage(),previousBlockHash);

    }

    @Override
    public String getApplicationMessageType() {
        return super.DECIDE_BLOCK_MESSAGE;
    }

    public int getRound() {
        return _round;
    }

    public String getHash() {return _hash;}

    public List<Integer> getQuorum() {
        return _quorum;
    }


    @Override
    public String toString (){
        return "TODO TODO: EMPTY METHOD";
    }

    @Override
    public String toString(int level) {
        return "TODO TODO: EMPTY METHOD";
    }
}
