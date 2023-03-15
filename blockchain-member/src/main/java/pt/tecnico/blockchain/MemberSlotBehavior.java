package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;
import pt.tecnico.blockchain.behavior.member.BehaviorController;
import pt.tecnico.blockchain.behavior.member.states.correct.CorrectState;
import pt.tecnico.blockchain.behavior.member.states.corrupt.ArbitraryState;
import pt.tecnico.blockchain.behavior.member.states.ommit.OmissionState;
import pt.tecnico.blockchain.behavior.member.states.signas.SignAsState;

import static pt.tecnico.blockchain.Config.BlockchainConfig.*;

public class MemberSlotBehavior {
    private BlockchainConfig config;
    private int slotDuration;
    private int pID;
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
                        BehaviorController.changeState(new OmissionState());
                        break;
                    case CORRUPT_MESSAGES:
                        BehaviorController.changeState(new ArbitraryState());
                        break;
                    case AUTHENTICATE_AS:
                        BehaviorController.changeState(new SignAsState(behavior.getSecond()));
                        break;
                    default:
                        break;
                }
            } else BehaviorController.changeState(new CorrectState()); // No commands for current slot
            slot++;
        }, slotDuration);
        task.start();
    }
}
