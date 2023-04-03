package pt.tecnico.blockchain.contracts;

import pt.tecnico.blockchain.Messages.Content;

public interface SmartContract {
    public String getContractID();
    public boolean assertTransaction(Content transaction);
}