package pt.tecnico.ibft;

public class Ibft {

    private int _pid;
    private int _consensusInstance;
    private int _round;
    private int _preparedRound;
    private String _preparedValue;
    private String _inputValue;
    
    public void leader(int consensusInstance, int round) {
        // any deterministic mapping from consensusInstance and round to the identifier of
        //   a process as long as it allows f+1 processes to eventually assume the leader role.
        return 1;
    }

    public void start(int consensusInstance, String value) {
        _consensusInstance = consensusInstance;
        _round = 1;
        _preparedRound = -1;
        _preparedValue = "";
        _inputValue = value;

        if (leader(_consensusInstance, _round) == _pid) {
            broadcastPrePrepare();
        }
        IbftTimer.start();
    }

    public void broadcastPrePrepare() {
        
    }
}
