package pt.tecnico.blockchain;


import org.junit.Ignore;
import org.junit.Test;
import pt.tecnico.blockchain.Path.BlockchainPaths;
import pt.tecnico.blockchain.Path.ModulePath;

import static pt.tecnico.blockchain.Path.BlockchainPaths.INITIATOR_MODULE_NAME;


public class LaunchClientTests {

    @Ignore
    @Test(expected = Throwable.class)
    public void launchClient() {
        String configFile = new ModulePath()
                .getParent()
                .append(INITIATOR_MODULE_NAME)
                .append("config.in")
                .getPath();

        String[] args = {"5", configFile ,"-debug"};
        Client.main(args);
    }
}
