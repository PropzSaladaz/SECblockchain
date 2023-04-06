package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.AppendBlockMessage;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransactionType;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.links.APLReturnMessage;
import pt.tecnico.blockchain.server.BlockchainMemberAPI;
import pt.tecnico.blockchain.server.SynchronizedTransactionPool;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MemberServicesImpl {

    public static ArrayList<Pair<String, Integer>> _clients;
    public static BlockchainMemberAPI _blockchainMemberAPI;

    public static void init(ArrayList<Pair<String, Integer>> clients, BlockchainMemberAPI blockchainMemberAPI){
        _clients = clients;
        _blockchainMemberAPI = blockchainMemberAPI;
    }

    public static boolean checkIfExistsClient(String address, int port){
        for (Pair<String,Integer> pair : _clients) if(pair.getFirst().equals(address) && pair.getSecond() == port) return true;
        return false;

    }

    public static void handleRequest(APLReturnMessage message) {
        try {
            Content content = message.getContent();
            ApplicationMessage appMsg = (ApplicationMessage) content;
            switch (appMsg.getApplicationMessageType()) {
                case ApplicationMessage.BLOCKCHAIN_TRANSACTION_MESSAGE:
                    Logger.logInfo("HandleRequest - memberImpl: " + ((BlockchainTransaction) content).toString(0));
                    _blockchainMemberAPI.parseTransaction((BlockchainTransaction) content);
                    break;
                case ApplicationMessage.CONSENSUS_INSTANCE_MESSAGE:
                    Ibft.handleMessage((ConsensusInstanceMessage) content, message.getSenderPid());
                    break;
                default:
                    Logger.logWarning("ERROR: Could not handle request. Expected either " +
                            "BLOCKCHAIN_TRANSACTION or CONSENSUS_INSTANCE_MESSAGE");
                    break;
            }
        } catch (ClassCastException e) {
            Logger.logWarning("Corrupted message: \n", e);
        } catch (RuntimeException e){
            Logger.logWarning("Non Authorization To Perform Operation\n", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


