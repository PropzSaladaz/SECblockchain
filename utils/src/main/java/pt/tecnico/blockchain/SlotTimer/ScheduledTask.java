package pt.tecnico.blockchain.SlotTimer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class ScheduledTask {
    
    private Runnable _task;
    private int interval;
    private Timer _timer;
    private Supplier<Boolean> stopConditionMet = () -> false;

    public ScheduledTask(Runnable task) {
        _task = task;
        this.interval = 0;
    }

    public ScheduledTask(Runnable task, int interval) {
        _task = task;
        this.interval = interval;
    }
    
    public void start() {
        _timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (stopConditionMet.get()) {
                    stop();
                } else _task.run();
            }
        };
        _timer.schedule(task, 0, interval);
    }

    public void setStopCondition(Supplier<Boolean> stopCond) {
        stopConditionMet = stopCond;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void stop() {
        _timer.cancel();
    }
}
