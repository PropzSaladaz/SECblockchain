package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Messages.Message;
import pt.tecnico.blockchain.Messages.tes.responses.TESResultMessage;
import pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction;
import pt.tecnico.blockchain.Messages.Content;
import java.util.List;

public class SignedQuorumAndBlockMessage extends Message implements Content {
    
    private List<Pair<Integer, byte[]>> _signatures;

    public SignedQuorumAndBlockMessage(Content block, List<Pair<Integer, byte[]>> signatures) {
        super(block);
        _signatures = signatures;
    }

    public boolean verifyBalanceProof(String transactionInvoker, String previousBalanceHash) {
        BlockchainBlock block = (BlockchainBlock) this.getContent();
        List<BlockchainTransaction> blockTransactions = block.getTransactions();
        int tx_i = 0;
        boolean foundVerifierInvokingTx = false;
        for (Pair<Integer, byte[]> sigPair : this.getSignaturePairs()) {
            if (!block.verifySignature(sigPair.getFirst(), sigPair.getSecond())) {
                return false;
            }
            if (foundVerifierInvokingTx && blockTransactions.get(tx_i).getSender().equals(transactionInvoker)) {
                if (((TESTransaction)blockTransactions.get(tx_i).getContent()).getBalanceHash().equals(previousBalanceHash)) {
                    foundVerifierInvokingTx = true;
                } else return false;
            }
        }
        return true;
    }

    public List<Pair<Integer, byte[]>> getSignaturePairs() {
        return _signatures;
    }

    @Override
    public boolean equals(Content another) {
        SignedQuorumAndBlockMessage proof = (SignedQuorumAndBlockMessage) another;
        return  _signatures.equals(proof.getSignaturePairs()) &&
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
