package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.Application;
import pt.tecnico.blockchain.KeyConverter;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;
import pt.tecnico.blockchain.Messages.tes.TESTransaction;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;

import java.util.Map;
import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;

public class BlockchainMemberAPI implements Application {
    private Blockchain chain;
    private DatagramSocket _socket;
    private Map<Integer,Pair<String,Integer>> _clientsPidToInfo;

    public BlockchainMemberAPI(DatagramSocket socket, Map<Integer,Pair<String,Integer>> clients) {
        chain = new Blockchain();
        _socket = socket;
        _clientsPidToInfo = clients;
    }

    @Override
    public void decide(Content message) {
        DecideBlockMessage decideMsg = (DecideBlockMessage) message;
        chain.decide(decideMsg.getContent());
        sendTransactionResultToClient(decideMsg);
    }

    @Override
    public boolean validateValue(Content value) {
        return chain.validateValue(value);
    }

    @Override
    public int getNextInstanceNumber() {
        return chain.getNextInstanceNumber();
    }

    @Override
    public void prepareValue(Content value) {
        chain.prepareValue(value);
    }

    public void sendTransactionResultToClient(Content message) {
        try {
//            System.out.println("Sending transaction result to client");
            BlockchainTransaction transaction = (BlockchainTransaction) message;
            TESTransaction tx = (TESTransaction) transaction.getContent();
            Pair<String,Integer> senderInfo = _clientsPidToInfo.get(RSAKeyStoreById.getPidFromPublic(KeyConverter.base64ToPublicKey(tx.getFrom())));
            AuthenticatedPerfectLink.send(_socket, message, senderInfo.getFirst(), senderInfo.getSecond());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}