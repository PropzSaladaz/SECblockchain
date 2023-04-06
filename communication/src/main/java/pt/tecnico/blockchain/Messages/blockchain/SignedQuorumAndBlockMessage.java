package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Messages.Message;
import pt.tecnico.blockchain.Messages.Content;
import java.util.List;

public class SignedQuorumAndBlockMessage extends Message implements Content {
    
    private List<Pair<Integer, byte[]>> _signatures;

    public SignedQuorumAndBlockMessage(Content block, List<Pair<Integer, byte[]>> signatures) {
        super(block);
        _signatures = signatures;
    }


    public List<Pair<Integer, byte[]>> getSignatures() {
        return _signatures;
    }

    @Override
    public boolean equals(Content another) {
        SignedQuorumAndBlockMessage proof = (SignedQuorumAndBlockMessage) another;
        return  _signatures.equals(proof.getSignatures()) &&
                this.getContent().equals(proof.getContent()); 
    }

    @Override
    public String toString(int tabs) {
        return  toStringWithTabs("TESBalanceProof: {", tabs) +
                toStringWithTabs("Block: " + this.getContent().toString(), tabs + 1) +
                toStringWithTabs("Signatures: " + _signatures.toString(), tabs + 1) +
                toStringWithTabs("}", tabs);
    }
}
