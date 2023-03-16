package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

public class BlockchainMessage implements Content {
    
    private String _message;
    private String _hash;

    public BlockchainMessage(String message) {
        _message = message;
    }
    
    public String getMessage() {
        return _message;
    }

    public String getHash() {
        return _hash;
    }

    public void setHash(String hash) {
        _hash = hash;
    }

    @Override
    public String toString (){
        return "BlockChainMessage: { "+
                "\n\tmessage: " + _message +
                "\n}";
    }

    @Override
    public String toString(int level) {
        String hash = (_hash == null) ? "" : _hash.substring(0, 15) + "...";
        return  toStringWithTabs("BlockchainMessage {" , level) +
                toStringWithTabs("message: " + _message, level + 1) +
                toStringWithTabs("hash: " + hash , level + 1) +
                toStringWithTabs("}", level);
    }
}
