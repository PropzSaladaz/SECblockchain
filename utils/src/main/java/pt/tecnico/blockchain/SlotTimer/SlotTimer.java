package pt.tecnico.blockchain.SlotTimer;

import java.util.Timer;

public class SlotTimer {
    
    private Callable _iCallable;
    private int slotDuration;
    private Timer _timer = new Timer();
    
    public SlotTimer(Callable iCallable, int slotDuration) {
        _iCallable = iCallable;
    }
    
    public void start() {
        _timer.schedule(_iCallable.getTask(), slotDuration);
    }

    public void stop() {
        _timer.cancel();
    }
}
