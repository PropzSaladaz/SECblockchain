package pt.tecnico.blockchain.Messages.tes;

public class ClientAccount {
    int _balance;
    public ClientAccount(){
        _balance = 1000;
    }
     public synchronized void  deposit(int value){_balance+=value;}

     public synchronized void withdrawal(int value) throws RuntimeException{
        if (_balance-value < 0) throw new RuntimeException("IMPOSSIBLE TO TRANSFER BALANCE IS NEGATIVE");
        else _balance-=value;
     }
     public synchronized int checkBalance(){return _balance;}


}
