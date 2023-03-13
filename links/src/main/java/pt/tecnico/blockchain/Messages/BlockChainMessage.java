package pt.tecnico.blockchain.Messages;

import java.util.Base64;

public class BlockChainMessage implements Content{
    private String _message;

    public BlockChainMessage(String message) {
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
