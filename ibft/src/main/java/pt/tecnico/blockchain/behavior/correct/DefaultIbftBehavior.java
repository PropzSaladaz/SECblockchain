package pt.tecnico.blockchain.behavior.correct;

import pt.tecnico.blockchain.Ibft;
import pt.tecnico.blockchain.IbftTimer;
import pt.tecnico.blockchain.Logger;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainBlock;
import pt.tecnico.blockchain.Messages.ibft.ConsensusInstanceMessage;
import pt.tecnico.blockchain.Messages.blockchain.DecideBlockMessage;

import pt.tecnico.blockchain.Keys.RSAKeyStoreById;

import static pt.tecnico.blockchain.IbftMessagehandler.broadcastMessage;

public class DefaultIbftBehavior {

    public static void handlePrePrepareRequest(ConsensusInstanceMessage message) {
        if (isValidMessageFromLeader(message) && Ibft.getApp().validateValue(message.getContent())) {
            ConsensusInstanceMessage msg = new ConsensusInstanceMessage(message.getConsensusInstance(),
            message.getRound(), Ibft.getPid(), message.getContent());
            msg.setMessageType(ConsensusInstanceMessage.PREPARE);
            msg.signMessage(RSAKeyStoreById.getPrivateKey(Ibft.getPid()), message.getContent());
            broadcastMessage(msg);
        }
        //IbftTimer.start(message.getRound()); Not needed anymore
    }

    private static boolean isValidMessageFromLeader(ConsensusInstanceMessage message) {
        return Ibft.leader(message.getConsensusInstance(), message.getRound()) == message.getSenderPID() &&
                message.getConsensusInstance() == Ibft.getConsensusInstance();
    }

    public static void handlePrepareRequest(ConsensusInstanceMessage message) {
        Ibft.addToPreparedQuorum(message);
        if (Ibft.hasValidPreparedQuorum()) {
            Logger.logDebug("PREPARE has same value of quorum");
            Ibft.setPreparedRound(message.getRound());
            Ibft.setPreparedValue(message.getContent());
            ConsensusInstanceMessage msg = new ConsensusInstanceMessage(message.getConsensusInstance(),
                    message.getRound(), Ibft.getPid(), message.getContent());
            msg.setMessageType(ConsensusInstanceMessage.COMMIT);
            msg.signMessage(RSAKeyStoreById.getPrivateKey(Ibft.getPid()), message.getContent());
            broadcastMessage(msg);
        }
    }

    public static void handleCommitRequest(ConsensusInstanceMessage message) {
        if (Ibft.hasSamePreparedValue(message)) {
            Logger.logDebug("COMMIT has same Prepared value");
            Ibft.addToCommitQuorum(message);
            if (Ibft.hasValidCommitQuorum()) {
                Logger.logDebug("Has valid COMMIT quorum");
                Content value = message.getContent();
                IbftTimer.stop();
                if (Ibft.getApp().validateValue(value)) {
                    Logger.logDebug("Value was validated, broadcasting... ");
                    Content content = Ibft.validateTransactions(message.getContent());
                    Ibft.getApp().prepareValue(content);
                    Ibft.getApp().decide(new DecideBlockMessage(
                            Ibft.getConsensusInstance(), content, Ibft.getCommitQuorum()
                    ));
                    Ibft.endInstance();
                }
            }
        }
    }
}
