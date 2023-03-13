package pt.tecnico.blockchain;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import pt.tecnico.blockchain.Path.BlockchainPaths;
import pt.tecnico.blockchain.Path.ModulePath;

public class BlockchainPathsTests {

    @Test
    public void memberKeyDirectoryPath() {
        String memberKeyPath = new ModulePath()
                .getParent()
                .append(BlockchainPaths.MEMBER_MODULE_NAME)
                .append("src")
                .append("main")
                .append("resources").getPath();
        assertEquals(memberKeyPath, BlockchainPaths.MEMBER_KEYDIR_PATH.getPath());
    }

    @Test
    public void clientKeyDirectoryPath() {
        String clientKeyPath = new ModulePath()
                .getParent()
                .append(BlockchainPaths.CLIENT_MODULE_NAME)
                .append("src")
                .append("main")
                .append("resources").getPath();
        assertEquals(clientKeyPath, BlockchainPaths.CLIENT_KEYDIR_PATH.getPath());
    }
}
