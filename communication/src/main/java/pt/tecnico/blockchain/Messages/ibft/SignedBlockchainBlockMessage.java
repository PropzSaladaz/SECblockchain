package pt.tecnico.blockchain.Messages.ibft;

import pt.tecnico.blockchain.Messages.Message;
import pt.tecnico.blockchain.Messages.MessageManager;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Crypto;
import pt.tecnico.blockchain.Messages.Content;

import java.security.PrivateKey;
import java.security.PublicKey;

public class SignedBlockchainBlockMessage extends Message implements Content {
    
    private byte[] _signature;

    public SignedBlockchainBlockMessage(Content block) {
        super(block);
    }

    public SignedBlockchainBlockMessage(byte[] signature, Content block) {
        super(block);
        _signature = signature;
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
                toStringWithTabs("signature: " + _signature , level + 1) +
                toStringWithTabs("block: " + getContent() , level + 1) +
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
    public void sign(PrivateKey privKey) {
        try {
            _signature = Crypto.getSignature(digestMessageFields(), privKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
