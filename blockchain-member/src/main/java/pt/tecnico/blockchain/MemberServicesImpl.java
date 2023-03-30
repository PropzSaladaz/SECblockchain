package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.AppendBlockMessage;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

import java.util.ArrayList;

public class MemberServicesImpl {

    static ArrayList<Pair<String, Integer>> _clients;


    public static void initClientandMembers(ArrayList<Pair<String, Integer>> clients){
        _clients = clients;
    }

    public static boolean checkIfExistsClient(String address, int port){
        for (Pair<String,Integer> pair : _clients) if(pair.getFirst().equals(address) && pair.getSecond() == port) return true;
        return false;

    }

    public static void handleRequest(Content message) {
        try {
            if (message != null) { // might be the case when getting out of omit state deliver returning null
                ApplicationMessage appMsg = (ApplicationMessage) message;
                switch (appMsg.getApplicationMessageType()) {
                    case ApplicationMessage.BLOCKCHAIN_TRANSACTION_MESSAGE:
                        BlockchainTransaction transaction = (BlockchainTransaction) message;
                        // TODO: Do something with the transaction
                        break;
                    case ApplicationMessage.APPEND_BLOCK_MESSAGE:
                        AppendBlockMessage msg = (AppendBlockMessage) message;
                        Ibft.start((BlockchainBlock) msg.getContent());
                        break;
                    case ApplicationMessage.CONSENSUS_INSTANCE_MESSAGE:
                        ConsensusInstanceMessage ibftMessage = (ConsensusInstanceMessage) message;
                        Ibft.handleMessage(ibftMessage);
                        break;
                    default:
                        System.out.println("ERROR: Could not handle request");
                        break;
                }
            }
        } catch (ClassCastException e) {
            System.out.println("Corrupted message\n");
        } catch (RuntimeException e){
            System.out.println("Non Authorization To Perform Operation\n");

        }
    }
}


