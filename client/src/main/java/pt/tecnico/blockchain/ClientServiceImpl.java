package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;

import pt.tecnico.blockchain.Messages.Content;

public class ClientServiceImpl {

    public static boolean verifyQuorumSignatures(List<ConsensusInstanceMessage> quorum) 
        throws IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {

        for (ConsensusInstanceMessage consensusMessage: quorum){
           if (!Crypto.verifySignature(
                    MessageManager.getContentBytes(consensusMessage.getContent()),
                    consensusMessage.getSignatureBytes(),
                    RSAKeyStoreById.getPublicKey(consensusMessage.getSenderPID()))){
               return false;
           }
        }
        return true;
    }

    public static void handleRequest(Content message) {
        ApplicationMessage msg = (ApplicationMessage) message;
        switch (msg.getApplicationMessageType()) {
            case ApplicationMessage.DECIDE_BLOCK_MESSAGE:
                try{
                    DecideBlockMessage decidedBlock = (DecideBlockMessage) message;
                    if (!verifyQuorumSignatures(decidedBlock.getQuorum())) throw new Exception();
                    System.out.println("APPENDED: " + decidedBlock.getContent().toString());
                    break;
                }catch(Exception e){
                    System.out.println("THE IBFT PROCESSES DID NOT AGREE APENDING THIS BLOCK");
                }
            default:
                System.out.println("ERROR: Could not handle request");
                break;
        }
    }
}
