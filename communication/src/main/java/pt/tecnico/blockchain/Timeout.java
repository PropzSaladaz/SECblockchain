package pt.tecnico.blockchain;

import pt.tecnico.blockchain.SlotTimer.ScheduledTask;

import java.util.ArrayList;

/**
 * Keeps all tasks that will be created inside Run
 * so that when timout occurs it can kill all created threads
 */
public class Timeout {
    private ArrayList<ScheduledTask> tasks = new ArrayList<>();
    private Runnable task;
    private long timeout;

    public Timeout(Runnable task, long timeoutMillis) {
        this.task = task;
        timeout = timeoutMillis;
    }

    public void run() {
        Thread thread = new Thread(task);
        thread.start();

        try {
            thread.join(timeout);
            killAllThreads();
        } catch (InterruptedException e) {
            killAllThreads();
            System.out.println("Timed out");
        }
    }

    private void killAllThreads() {
        for (ScheduledTask t : tasks) {
            t.stop();
        }
    }

    public void addInternalScheduleTask(ScheduledTask t) {
        tasks.add(t);
    }


}
