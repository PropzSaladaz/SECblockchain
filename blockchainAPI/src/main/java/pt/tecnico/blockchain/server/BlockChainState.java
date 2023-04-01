package pt.tecnico.blockchain.server;

import java.util.HashMap;
import java.util.Map;

public class BlockChainState {
    private final Map<String,ContractI> _contracts;
    private static int _contractId;

    public BlockChainState(){
        _contracts = new HashMap<>();
        _contractId = 1;
    }

    public void addContract(ContractI contract,String minerKey){
        _contracts.put(String.valueOf(_contractId),contract);
        _contractId+=1;
        contract.createMinerAccount(minerKey);
    }

    public void removeContract(String id){
        _contracts.remove(id);
    }

    public ContractI getContract(String id){
        return _contracts.get(id);
    }

}
