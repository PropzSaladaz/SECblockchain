package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;
import pt.tecnico.blockchain.behavior.IbftBehaviorController;
import pt.tecnico.blockchain.behavior.correct.IbftCorrect;
import pt.tecnico.blockchain.behavior.corrupt.IbftCorrupt;
import pt.tecnico.blockchain.behavior.LinkBehaviorController;
import pt.tecnico.blockchain.behavior.states.correct.CorrectState;
import pt.tecnico.blockchain.behavior.states.corrupt.ArbitraryState;
import pt.tecnico.blockchain.behavior.states.ommit.OmissionState;
import pt.tecnico.blockchain.behavior.states.signas.SignAsState;

import static pt.tecnico.blockchain.Config.BlockchainConfig.*;

public class MemberSlotBehavior {
    private final BlockchainConfig config;
    private final int slotDuration;
    private final int pID;
    private int slot;

    public MemberSlotBehavior(BlockchainConfig config, int pID) {
        this.config = config;
        this.slotDuration = config.getSlotDuration();
        this.pID = pID;
        slot = 0;
    }

    public void track() {
        ScheduledTask task = new ScheduledTask( () -> {
            Pair<String, Integer> behavior = config.getBehaviorInSlotForProcess(slot, pID);
            if (behavior != null) {
                switch(behavior.getFirst()) {
                    case OMIT_MESSAGES:
                        LinkBehaviorController.changeState(new OmissionState());
                        break;
                    case CORRUPT_MESSAGES:
                        LinkBehaviorController.changeState(new ArbitraryState());
                        IbftBehaviorController.changeState(new IbftCorrupt());
                        break;
                    case AUTHENTICATE_AS:
                        LinkBehaviorController.changeState(new SignAsState(behavior.getSecond()));
                        break;
                    default:
                        break;
                }
            } else {
                LinkBehaviorController.changeState(new CorrectState()); // No commands for current slot
                IbftBehaviorController.changeState(new IbftCorrect());
            }
            slot++;
        }, slotDuration);
        task.start();
    }
}
