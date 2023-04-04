package pt.tecnico.blockchain.Messages.tes;

import pt.tecnico.blockchain.Messages.Content;

public class ClientAccount {
    
    Integer _balance;
    Integer _previousBalance;
    Content _lastBlockToWriteOnBalance;

    public ClientAccount() {
        _balance = 1000;
        _previousBalance = _balance;
        _lastBlockToWriteOnBalance = null;
    }

    public synchronized void deposit(int value) {
        _balance += value;
    }

    public synchronized void withdrawal(int value) throws RuntimeException {
        if (_balance-value < 0) throw new RuntimeException("IMPOSSIBLE TO TRANSFER BALANCE IS NEGATIVE");
        else _balance -= value;
    }
    
    public Integer getCurrentBalance() {
        return _balance;
    }

    public Integer getPreviousBalance() {
        return _previousBalance;
    }

    public Content getLastBlockToWriteOnBalance() {
        return _lastBlockToWriteOnBalance;
    }
}
