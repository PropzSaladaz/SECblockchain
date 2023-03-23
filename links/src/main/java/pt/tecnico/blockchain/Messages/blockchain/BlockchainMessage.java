package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;

public class BlockchainMessage implements Content {

    private int _port;
    private String _address;
    private String _message;
    private String _hash;

    public BlockchainMessage(String message) {
        _message = message;
    }

    public BlockchainMessage(String message,String address, int port) {
        _message = message;
        _address = address;
        _port = port;
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

    public int getPort() {return _port;}

    public void setPort(int port) {this._port = port;}

    public String getAddress() {return _address;}

    public void setAddress(String address) {this._address = address;}

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

    @Override
    public boolean equals(Content another) {
        try {
            BlockchainMessage m = (BlockchainMessage) another;
            return _port == m.getPort() &&
                    _address.equals(m.getAddress()) &&
                    _message.equals(m.getMessage()) &&
                    _hash.equals(m.getHash());
        } catch(ClassCastException  e) {
            return false;
        }
    }
}
