package pt.tecnico.blockchain.server;


import pt.tecnico.blockchain.Messages.Content;

public interface ContractI {
    boolean validateBlock(Content content,Boolean isMiner,String memberKey);
    void checkBalance(Content content);
    void createMinerAccount(String minerKey);

}

