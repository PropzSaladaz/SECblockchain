package pt.tecnico.blockchain.Messages.ibft;

import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.Message;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.Content;

import java.security.PrivateKey;
import java.security.PublicKey;

public class SignedBlockchainBlockMessage extends Message implements Content {
    
    private byte[] _signature;
    private Integer _signerPID;

    public SignedBlockchainBlockMessage() {
    }

    public SignedBlockchainBlockMessage(Content block) {
        super(block);
    }

    public Integer getSignerPID() {
        return _signerPID;
    }

    public void setSignature(byte[] signature) {
        _signature = signature;
    }

    public byte[] getSignature() {
        return _signature;
    }

    @Override
    public String toString (){
        return toString(0);
    }

    @Override
    public String toString(int level) {
        return  toStringWithTabs("SignedBlockchainBlockMessage {" , level) +
                toStringWithTabs("signature: " + Crypto.base64(_signature, 15) , level + 1) +
                getContent().toString(level+1) +
                toStringWithTabs("}", level);
    }

    @Override
    public boolean equals(Content another) {
        try {
            SignedBlockchainBlockMessage m = (SignedBlockchainBlockMessage) another;
            return this.getContent().equals(m.getContent());
        } catch(ClassCastException  e) {
            return false;
        }
    }

    @Override
    public byte[] digestMessageFields() {
        try {
            return MessageManager.getContentBytes(this.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void sign(Integer signerPID) {
        try {
            _signature = Crypto.getSignature(digestMessageFields(), RSAKeyStoreById.getPrivateKey(signerPID));
            _signerPID = signerPID;
            Logger.logInfo(" Signing SignedBlock: " + getContent().toString(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
