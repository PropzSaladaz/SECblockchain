package pt.tecnico.blockchain;

import pt.tecnico.blockchain.SlotTimer.*;
import java.util.TimerTask;

public class BlockchainMemberFrontend implements Callable {
    
    public TimerTask getTask() {
        return new TimerTask() {
            @Override
            public void run() {
                broadcastClientRequests();
            }
        };
    }

    public void broadcastClientRequests() {

    }
}
