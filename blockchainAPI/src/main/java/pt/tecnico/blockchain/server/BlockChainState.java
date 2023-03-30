package pt.tecnico.blockchain.server;

import java.util.HashMap;
import java.util.Map;

public class BlockChainState {
    Map<Integer,ContractI> _contracts;
    static int _contractId;
    public BlockChainState(){
        _contracts = new HashMap<>();
        _contractId = 1;
    }

    public void addContract(ContractI contract){
        _contracts.put(_contractId,contract);
        _contractId+=1;
    }

    public void removeContract(int id){
        _contracts.remove(_contractId);
    }

    public ContractI getContract(int id){
        return _contracts.get(id);
    }

}
