package pt.tecnico.blockchain;

import java.security.PublicKey;

/**
 * Clients of TES will use the methods from this interface
 */
public interface ContractAPI {
    void createAccount(int gasPrice, int gasLimit);
    void transfer(PublicKey destination, int amount, int gasPrice, int gasLimit);
    void checkBalance(int gasPrice, int gasLimit);
    void waitForMessages();
}
