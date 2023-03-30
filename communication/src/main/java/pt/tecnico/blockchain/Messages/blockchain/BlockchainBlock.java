package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

import java.util.List;

public class BlockchainBlock implements Content {

    // TODO needed??
    private int _port;
    private String _address;

    private List<BlockchainTransaction> _transactions;
    private String _hash;

    public BlockchainBlock(List<BlockchainTransaction> transactions) {
        _transactions = transactions;
    }

    public BlockchainBlock(List<BlockchainTransaction> transactions, String address, int port) {
        _transactions = transactions;
        _address = address;
        _port = port;
    }
    
    public List<BlockchainTransaction> getTransactions() {
        return _transactions;
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
        return toString(0);
    }

    @Override
    public String toString(int level) {
        String hash = (_hash == null) ? "" : _hash.substring(0, 15) + "...";
        StringBuilder txns = new StringBuilder();
        for (BlockchainTransaction m : _transactions) {
            txns.append(m.toString(level+2));
        }
        return  toStringWithTabs("BlockchainMessage {" , level) +
                toStringWithTabs("transactions: " + txns , level + 1) +
                toStringWithTabs("hash: " + hash , level + 1) +
                toStringWithTabs("}", level);
    }

    @Override
    public boolean equals(Content another) {
        try {
            BlockchainBlock m = (BlockchainBlock) another;
            return _port == m.getPort() &&
                    _address.equals(m.getAddress()) &&
                    _hash.equals(m.getHash());
        } catch(ClassCastException  e) {
            return false;
        }
    }
}
