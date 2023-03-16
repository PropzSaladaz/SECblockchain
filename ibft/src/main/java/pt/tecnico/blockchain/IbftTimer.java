package pt.tecnico.blockchain;

import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

public class IbftTimer {

    private static Timer timer = new Timer();

    private static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            System.out.println("Round Timer Expired: Triggering <ROUND-CHANGE>");
        }
    };

    public static void start(int round) {
        timer.schedule(task, (long)Math.exp(round));
    }

    public static void stop() {
        System.out.println("Stopping Round Timer before <ROUND-CHANGE>");
        timer.cancel();
    }
}
