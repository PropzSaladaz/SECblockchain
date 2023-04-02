package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.tes.CheckBalance;
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
        return _clientAccounts.get(publicKey) != null;
    }

    public boolean validateSignature(TESTransaction transaction) {
        return transaction.checkSign();
    }

    public boolean validateTransfer(Transfer transfer){
        return transfer.getAmount() > 0 && hasClient(transfer.getDestinationAddress());
    }
    public int getBalance(String sender){
        return _clientAccounts.get(sender).checkBalance();
    }

    public void checkBalance(Content content){
        TESTransaction transaction = (TESTransaction) content;
        if (transaction.getType().equals(TESTransaction.CHECK_BALANCE)){
            CheckBalance checkBalance = (CheckBalance) transaction;
            checkBalance.setAmount(getBalance(transaction.getSender()));
        }
    }

    public void createMinerAccount(String minerKey){
        _clientAccounts.put(minerKey, new ClientAccount());
    }


    @Override
    public boolean validateBlock(Content content, Boolean isMiner,String memberKey) {
        TESTransaction transaction = (TESTransaction) content;
        switch (transaction.getType()) {
            case TESTransaction.CREATE_ACCOUNT:
                if (validateSignature(transaction) && !hasClient(transaction.getSender())){
                    _clientAccounts.put(transaction.getSender(), new ClientAccount());
                    if(isMiner) _clientAccounts.get(memberKey).deposit(1000);
                    return true;
                }else return false;
            case TESTransaction.TRANSFER:
                Transfer transfer = (Transfer) transaction;
                if (validateSignature(transaction) && hasClient(transaction.getSender()) && validateTransfer(transfer)){
                    _clientAccounts.get(transaction.getSender()).withdrawal(transfer.getAmount());
                    _clientAccounts.get(transfer.getDestinationAddress()).deposit(transfer.getAmount());
                    if(isMiner) _clientAccounts.get(memberKey).deposit(5000);
                    return true;
                }else return false;
            case TESTransaction.CHECK_BALANCE:
                if(validateSignature(transaction) && hasClient(transaction.getSender())){
                    if(isMiner) _clientAccounts.get(memberKey).deposit(3000);
                    return true;
                }
                return false;
            default:
                System.out.println("ERROR: Could not handle request");
                return false;
        }
    }
}
