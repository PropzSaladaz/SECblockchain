package pt.tecnico.blockchain.Messages;

import java.util.Base64;

public class BlockchainMessage extends Message implements Content {
    
    private String _message;

    public BlockchainMessage(String message) {
       _message = message;
    }

    @Override
    public String getContentType() {
        return ContentType.APPEND_BLOCK;
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
