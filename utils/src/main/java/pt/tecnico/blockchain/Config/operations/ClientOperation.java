package pt.tecnico.blockchain.Config.operations;

public abstract class ClientOperation {
    private int gasPrice;
    private int gasLimit;
    private String type;

    public ClientOperation(String type) {
        this.type = type;
    }

    public ClientOperation(String type, int gasPrice, int gasLimit) {
        this.type = type;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
    }

    public void setGasPrice(int gasPrice) {
        this.gasPrice = gasPrice;
    }

    public void setGasLimit(int gasLimit) {
        this.gasLimit = gasLimit;
    }

    public int getGasPrice() {
        return gasPrice;
    }

    public int getGasLimit() {
        return gasLimit;
    }

    public String getType() {
        return type;
    }
}
