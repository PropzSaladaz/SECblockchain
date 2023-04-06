package pt.tecnico.blockchain.Messages.blockchain;

import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Messages.Message;
import pt.tecnico.blockchain.Messages.tes.responses.TESResultMessage;
import pt.tecnico.blockchain.Messages.tes.transactions.TESTransaction;
import pt.tecnico.blockchain.Messages.tes.transactions.TransferTransaction;
import pt.tecnico.blockchain.Messages.Content;
import java.util.List;

public class QuorumSignedBlockMessage extends Message implements Content {
    
    private List<Pair<Integer, byte[]>> _signatures;

    public QuorumSignedBlockMessage(Content block, List<Pair<Integer, byte[]>> signatures) {
        super(block);
        _signatures = signatures;
    }


    private int updateCurrentBalance(String verifier, int balance, TransferTransaction tx) {
        int newBalance = balance;
        if (tx.getSender().equals(verifier)) {
            newBalance -= tx.getAmount();
        } else if (tx.getDestinationAddress().equals(verifier)) {
            newBalance += tx.getAmount();
        }
        return newBalance;
    }

    public int assertTesAccountBalance(String verifier, int previousBalance) {

        BlockchainBlock block = (BlockchainBlock) this.getContent();
        List<BlockchainTransaction> blockTransactions = block.getTransactions();
        int current_balance = previousBalance;

        for (Pair<Integer, byte[]> sigPair : this.getSignaturePairs()) {
            if (!block.verifySignature(sigPair.getFirst(), sigPair.getSecond())) {
                return -1;
            }
        }
        for (BlockchainTransaction tx : blockTransactions) {
            if (current_balance == -1) break;
            if (tx.getContractID().equals("TESCONTRACTID")) {
                TESTransaction tesTx = (TESTransaction) tx.getContent();
                switch (tesTx.getType()) {
                    case TESTransaction.TRANSFER:
                        current_balance = updateCurrentBalance(verifier, current_balance, (TransferTransaction)tesTx);
                        break;
                    case TESTransaction.CREATE_ACCOUNT:
                        break;
                    default:
                        Logger.logDebug("Error in TESBalanceProof verification: No transaction matches an expected type");
                        break;
                }
            }
        }
        return current_balance;
    }

    public List<Pair<Integer, byte[]>> getSignaturePairs() {
        return _signatures;
    }

    @Override
    public boolean equals(Content another) {
        QuorumSignedBlockMessage proof = (QuorumSignedBlockMessage) another;
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
