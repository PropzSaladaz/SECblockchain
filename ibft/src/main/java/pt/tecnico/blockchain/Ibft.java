package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;

public class Ibft {

    private int _pid;
    private int _consensusInstance;
    private int _round;
    private int _preparedRound;
    private BlockchainMessage _preparedValue;
    private BlockchainMessage _inputValue;
    private IbftTimer timer = new IbftTimer();

    public int leader(int consensusInstance, int round) {
        // any deterministic mapping from consensusInstance and round to the identifier of
        //   a process as long as it allows f+1 processes to eventually assume the leader role.
        return 1;
    }

    public void start(BlockchainMessage inputValue) {
        _consensusInstance = consensusInstance;
        _round = 1;
        _preparedRound = -1;
        _inputValue = inputValue;

        if (leader(_consensusInstance, _round) == _pid) {
            broadcastPrePrepare();
        }
        startTimer(_round);
    }

    public void startTimer(int round) {
        timer.start(round);
    }

    public void stopTimer() {
        timer.stop();
    }

    public int getPID() {
        return _pid;
    }

    public int getConsensusInstance() {
        return _consensusInstance;
    }

    public int getRound() {
        return _round;
    }

    public void setRound(int value) {
        _round = value;
    }

    public int getPreparedRound() {
        return _preparedRound;
    }

    public void setPreparedRound(int value) {
        _preparedRound = value;
    }

    public BlockchainMessage getPreparedValue() {
        return _preparedValue;
    }

    public void setPreparedValue(BlockchainMessage value) {
        _preparedValue = value;
    }

    public BlockchainMessage getValue() {
        return _inputValue;
    }

    public void broadcastPrePrepare() {
        
    }
}
