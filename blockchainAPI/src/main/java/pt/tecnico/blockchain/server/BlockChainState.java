package pt.tecnico.blockchain.server;

import java.util.HashMap;
import java.util.Map;

public class BlockChainState {
    Map<String,ContractI> _contracts;
    static int _contractId;
    public BlockChainState(){
        _contracts = new HashMap<>();
        _contractId = 1;
    }

    public void addContract(ContractI contract){
        _contracts.put(String.valueOf(_contractId),contract);
        _contractId+=1;
    }

    public void removeContract(String id){
        _contracts.remove(id);
    }

    public ContractI getContract(String id){
        return _contracts.get(id);
    }

}
