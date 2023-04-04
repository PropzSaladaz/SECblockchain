package pt.tecnico.blockchain.Messages.tes;

public class ClientAccount {
    int _balance;

    public ClientAccount(){
        _balance = 1000;
    }

    public synchronized void  deposit(int value){_balance+=value;}

    public synchronized boolean withdrawal(int value) throws RuntimeException{
        if (_balance-value < 0) return false;
        else _balance-= value;
        return true;
    }

    public synchronized int getBalance(){
        return _balance;
    }

    public synchronized boolean hasBalanceGreaterThan(int amount){
        return _balance > amount;
    }


}
