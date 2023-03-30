package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

import java.util.List;

public class BlockchainBlock implements Content {

    private List<BlockchainTransaction> _transactions;
    private String _hash;

    public BlockchainBlock(List<BlockchainTransaction> transactions) {
        _transactions = transactions;
    }

    public BlockchainBlock(List<BlockchainTransaction> transactions, String address, int port) {
        _transactions = transactions;
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
            return _hash.equals(m.getHash());
        } catch(ClassCastException  e) {
            return false;
        }
    }
}
