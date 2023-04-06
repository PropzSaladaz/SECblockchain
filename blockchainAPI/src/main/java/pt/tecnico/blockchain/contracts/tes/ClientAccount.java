package pt.tecnico.blockchain.contracts.tes;

import pt.tecnico.blockchain.Messages.Content;

public class ClientAccount {

    Integer _balance;
    Integer _previousBalance;
    Content _balanceProof;

    public ClientAccount() {
        _balance = 1000;
        _previousBalance = _balance;
        _balanceProof = null;
    }

    
    public synchronized void deposit(int value) {
        _balance += value;
    }
    
    public synchronized boolean withdrawal(int value) throws RuntimeException {
        if (_balance-value < 0) return false;
        else _balance -= value;
        return true;
    }
    
    public Integer getCurrentBalance() {
        return _balance;
    }
    
    public Integer getPreviousBalance() {
        return _previousBalance;
    }
    
    public Content getBalanceProof() {
        return _balanceProof;
    }
    
    public synchronized void updateBalanceProof(Content balanceProof) {
        _balanceProof = balanceProof;
    }

    public boolean hasBalanceGreaterThan(int amount){
        return _balance > amount;
    }
}
