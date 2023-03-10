package pt.tecnico.ibft;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

public static class IbftTimer {

    private String state = "expired";
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            state = "expired";
        }
    };

    public static void start(int round) {
        Timer timer = new Timer();
        state = "running";
        timer.schedule(task, exp(round));
    }
}
