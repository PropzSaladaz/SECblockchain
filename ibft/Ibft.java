package pt.tecnico.ibft;

public class Ibft {

    private int _pid;
    private int _blockNumber;
    private int _round;
    private int _preparedRound;
    private String _preparedValue;
    private String _inputValue;
    
    public void leader(int blockNumber, int round) {
        // any deterministic mapping from blockNumber and round to the identifier of
        //   a process as long as it allows f+1 processes to eventually assume the leader role.
        return 1;
    }

    public void start(int blockNumber, String value) {
        _blockNumber = blockNumber;
        _round = 1;
        _preparedRound = -1;
        _preparedValue = "";
        _inputValue = value;

        if (leader(_blockNumber, _round) == _pid) {
            broadcastPrePrepare();
        }
        IbftTimer.start();
    }

    public void broadcastPrePrepare() {
        
    }
}
