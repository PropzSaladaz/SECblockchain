package pt.tecnico.blockchain;

import pt.tecnico.blockchain.console.Console;
import pt.tecnico.blockchain.console.MavenConsole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static pt.tecnico.blockchain.ErrorMessage.COULD_NOT_INIT_PROCESS;
import static pt.tecnico.blockchain.ErrorMessage.INCORRECT_INITIATOR_ARGUMENTS;


public class Initiator {
    private static Path rootModule = Paths.get(new File("").getAbsolutePath());
    private static String memberModule = rootModule.getParent() + File.separator + "blockchain-member";
    private static String clientModule = rootModule.getParent() + File.separator + "client";

    private static String DEBUG_STRING = "-debug";
    private static boolean DEBUG = false;

    public static void main( String[] args ) throws BlockChainException, IOException, InterruptedException {
        if (!correctNumberArgs(args)) throw new BlockChainException(INCORRECT_INITIATOR_ARGUMENTS);
        setDebugMode(args);
        BlockchainConfig config = new BlockchainConfig();
        config.setFromRelativePath(args[0]);
        initProcesses(config);
    }

    private static void initProcesses(BlockchainConfig config) throws BlockChainException {
        initMembers(config);
        initClients(config);
    }

    private static void initMembers(BlockchainConfig config) {
        try {
            ArrayList<Integer> ids = config.getMemberIds();
            String debug = DEBUG ? DEBUG_STRING : "";
            for (int id : ids) {
                Console console = new MavenConsole(memberModule, String.valueOf(id), config.getFilePath(), debug);
                console.launch();
            }
        } catch (IOException e) {
            throw new BlockChainException(COULD_NOT_INIT_PROCESS, e.getMessage());
        }
    }

    private static void initClients(BlockchainConfig config) {
        try {
            ArrayList<Integer> ids = config.getClientIds();
            String debug = DEBUG ? DEBUG_STRING : "";
            for (int id : ids) {
                Console console = new MavenConsole(clientModule, String.valueOf(id), config.getFilePath(), debug);
                console.launch();
            }
        } catch (IOException e) {
            throw new BlockChainException(COULD_NOT_INIT_PROCESS, e.getMessage());
        }
    }

    private static boolean correctNumberArgs(String[] args) {
        return args.length >= 1 && args.length <= 2;
    }

    private static void setDebugMode(String[] args) {
        if (args.length == 2 && args[1].equals(DEBUG_STRING))
            DEBUG = true;
    }
}
