package pt.tecnico.blockchain.contracts;

import pt.tecnico.blockchain.Messages.Content;

public interface SmartContract {
    String getContractID();
    boolean assertTransaction(Content transaction,String minerKey);
}