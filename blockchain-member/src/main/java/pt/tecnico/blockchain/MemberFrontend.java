package pt.tecnico.blockchain;

import pt.tecnico.blockchain.SlotTimer.*;
import pt.tecnico.blockchain.Messages.*;

import java.util.TimerTask;

public class MemberFrontend implements Callable {

    public TimerTask getTask() {
        return new TimerTask() {
            @Override
            public void run() {
                performArbitraryBehaviour();
            }
        };
    }

    public static void performArbitraryBehaviour() {

    }

    public static void broadcastMessage(Message message) {

    }
}
