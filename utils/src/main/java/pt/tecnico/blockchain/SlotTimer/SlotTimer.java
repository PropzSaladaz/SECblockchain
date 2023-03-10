package pt.tecnico.blockchain.SlotTimer;

import java.util.Timer;

public class SlotTimer {
    
    private Callable _iCallable;
    private int slotDuration;
    
    public SlotTimer(Callable iCallable, int slotDuration) {
        _iCallable = iCallable;
    }
    
    public void start() {
        Timer timer = new Timer();
        timer.schedule(_iCallable.getTask(), slotDuration);
    }
}
