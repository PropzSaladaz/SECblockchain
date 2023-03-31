package pt.tecnico.blockchain.server;


import pt.tecnico.blockchain.Messages.tes.TESTransaction;

public interface ContractI {
    boolean validateBlock(TESTransaction transaction);

}

