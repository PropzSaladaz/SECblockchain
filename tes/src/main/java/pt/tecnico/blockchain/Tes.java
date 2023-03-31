package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.tes.ClientAccount;
import pt.tecnico.blockchain.Messages.tes.TESTransaction;
import pt.tecnico.blockchain.Messages.tes.Transfer;
import pt.tecnico.blockchain.server.ContractI;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class Tes implements ContractI {

    Map<String, ClientAccount> _clientAccounts;

    public Tes(){
        _clientAccounts = new HashMap<>();
    }

    public boolean hasClient(String publicKey){
        if (_clientAccounts.get(publicKey)==null) return false;
        else return true;
    }

    public boolean validateSignature(TESTransaction transaction) {
        return transaction.checkSign();
    }

    public boolean validateTransfer(Transfer transfer){
        if (transfer.getAmount() <= 0 || !hasClient(transfer.getDestinationAddress())) return false;
        else return true;
    }


    @Override
    public boolean validateBlock(TESTransaction transaction) {
        switch (transaction.getType()) {
            case TESTransaction.CREATE_ACCOUNT:
                if (validateSignature(transaction) && !hasClient(transaction.getSender())){
                    _clientAccounts.put(transaction.getSender(), new ClientAccount());
                    return true;
                }else return false;
            case TESTransaction.TRANSFER:
                Transfer transfer = (Transfer) transaction;
                if (validateSignature(transaction) && hasClient(transaction.getSender()) && validateTransfer(transfer)){
                    _clientAccounts.get(transaction.getSender()).withdrawal(transfer.getAmount());
                    _clientAccounts.get(transfer.getDestinationAddress()).deposit(transfer.getAmount());
                    return true;
                }else return false;
            case TESTransaction.CHECK_BALANCE:
                if (validateSignature(transaction) && hasClient(transaction.getSender())) return true;
                else return false;
            default:
                System.out.println("ERROR: Could not handle request");
                return false;
        }
    }
}
