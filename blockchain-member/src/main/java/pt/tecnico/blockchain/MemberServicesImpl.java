package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.AppendBlockMessage;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
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
                    BlockchainTransaction transaction = (BlockchainTransaction) content;
                    if(transaction.getOperationType().equals(BlockchainTransaction.UPDATE)){
                        Content block = _blockchainMemberAPI.addTransactionAndGetBlockIfReady(transaction);
                        if (block != null) Ibft.start(block);
                    }else if(transaction.getOperationType().equals(BlockchainTransaction.STRONG_READ)){
                        _blockchainMemberAPI.executeStrongRead(transaction);
                    }
                    break;
                case ApplicationMessage.CONSENSUS_INSTANCE_MESSAGE:
                    ConsensusInstanceMessage ibftMessage = (ConsensusInstanceMessage) content;
                    Ibft.handleMessage(ibftMessage, message.getSenderPid());
                    break;
                default:
                    Logger.logWarning("ERROR: Could not handle request");
                    break;
            }
        } catch (ClassCastException e) {
            System.out.println("Corrupted message\n");
        } catch (RuntimeException e){
            System.out.println("Non Authorization To Perform Operation\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


