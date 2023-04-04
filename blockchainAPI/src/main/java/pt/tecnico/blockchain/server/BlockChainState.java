package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.contracts.SmartContract;

import java.util.HashMap;
import java.util.Map;

public class BlockChainState {
    
    private final Map<String, SmartContract> _contracts;

    public BlockChainState(){
        _contracts = new HashMap<>();
    }

    public void addContract(SmartContract contract){
        _contracts.put(contract.getContractID(), contract);
    }

    public void removeContract(String id){
        _contracts.remove(id);
    }

    public boolean existContract(String id) {
        return _contracts.get(id) != null;
    }

    public SmartContract getContract(String id) {
        if (_contracts.containsKey(id)) {
            return _contracts.get(id);
        }
        return null;
    }
}
