package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ApplicationMessage;

public class BlockchainMessage implements Content {
    
    private String _message;

    public BlockchainMessage(String message) {
       _message = message;
    }
    
    public String getMessage() {
        return _message;
    }

    @Override
    public String toString (){
        return "BlockChainMessage: { "+
                "\n\tmessage: " + _message +
                "\n}";
    }

    @Override
    public String toString(int level) {
        return  toStringWithTabs("BlockchainMessage {" , level) +
                toStringWithTabs("message: " + _message, level + 1) +
                toStringWithTabs("}", level);
    }
}
