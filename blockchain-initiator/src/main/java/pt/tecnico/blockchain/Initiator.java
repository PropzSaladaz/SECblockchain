package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Path.ModulePath;
import pt.tecnico.blockchain.Path.Path;
import pt.tecnico.blockchain.console.Console;
import pt.tecnico.blockchain.console.MavenConsole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import static pt.tecnico.blockchain.ErrorMessage.COULD_NOT_INIT_PROCESS;
import static pt.tecnico.blockchain.ErrorMessage.INCORRECT_INITIATOR_ARGUMENTS;


public class Initiator {
    private static final Path rootModule = new ModulePath();
    private static final String memberModule = rootModule.getParent().append("blockchain-member").getPath();
    private static final String clientModule = rootModule.getParent().append("client").getPath();

    private static final BlockchainConfig config = new BlockchainConfig();;
    private static final String DEBUG_STRING = "-debug";
    private static boolean DEBUG = false;


    public static void main( String[] args ) throws BlockChainException, IOException {
        if (!correctNumberArgs(args)) throw new BlockChainException(INCORRECT_INITIATOR_ARGUMENTS);
        setDebugMode(args);
        config.setFromRelativePath(args[0]);
        initProcesses();
    }

    private static void initProcesses() throws BlockChainException {
        try {
            initMembers();
            initClients();
        } catch (IOException e) {
            throw new BlockChainException(COULD_NOT_INIT_PROCESS, e.getMessage());
        }

    }

    private static void initMembers() throws IOException {
        ArrayList<Integer> ids = config.getMemberIds();
        String debug = DEBUG ? DEBUG_STRING : "";
        for (int id : ids) {
            Console console = new MavenConsole(String.valueOf(id), config.getFilePath(), debug);
            console.setDirectory(memberModule);
            console.setTitle("Member " + id);
            console.launch();
        }
    }

    private static void initClients() throws IOException {
        ArrayList<Integer> ids = config.getClientIds();
        String debug = DEBUG ? DEBUG_STRING : "";
        for (int id : ids) {
            Console console = new MavenConsole(String.valueOf(id), config.getFilePath(), debug);
            console.setDirectory(clientModule);
            console.setTitle("Client " + id);
            console.launch();
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
