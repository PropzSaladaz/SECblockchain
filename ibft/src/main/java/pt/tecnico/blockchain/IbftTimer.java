package pt.tecnico.blockchain;

import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

public class IbftTimer {

    private Timer timer = new Timer();
    private String state = "expired";

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            state = "expired";
        }
    };

    public void start(int round) {
        state = "running";
        timer.schedule(task, (long)Math.exp(round));
    }

    public void stop() {
        timer.cancel();
    }
}
