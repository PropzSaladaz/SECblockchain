package pt.tecnico.blockchain.Messages;

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

    public String toString (){
        return "Message: "+ _message;
    }
}
