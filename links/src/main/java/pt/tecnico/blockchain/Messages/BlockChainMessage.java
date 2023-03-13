package pt.tecnico.blockchain.Messages;

public class BlockChainMessage implements Content{
    private String _message;

    public BlockChainMessage(String message) {
       _message = message;

    }
    public String getMessage() {
        return _message;
    }

    public String toString (){

        return "Message: "+ _message;
    }




}
