package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Keys.KeyFilename;
import pt.tecnico.blockchain.Path.BlockchainPaths;
import pt.tecnico.blockchain.Path.ModulePath;
import pt.tecnico.blockchain.Path.Path;
import pt.tecnico.blockchain.console.Console;
import pt.tecnico.blockchain.console.MavenConsole;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pt.tecnico.blockchain.ErrorMessage.COULD_NOT_INIT_PROCESS;
import static pt.tecnico.blockchain.ErrorMessage.INCORRECT_INITIATOR_ARGUMENTS;


public class Initiator {
    private static Pattern ARGUMENTS = Pattern.compile("^(?<configFile>[\\w.]+)" +
            "(?<generate>\\s+-gen)?(?<debug>\\s+-debug)?$");
    private static final Path rootModule = new ModulePath();
    private static final String DEBUG_FLAG = "-debug";
    private static final String GENERATE_FLAG = "-gen";
    private static final String memberModule = rootModule.getParent().append(BlockchainPaths.MEMBER_MODULE_NAME).getPath();
    private static final String clientModule = rootModule.getParent().append(BlockchainPaths.CLIENT_MODULE_NAME).getPath();
    private static final BlockchainConfig config = new BlockchainConfig();

    private static boolean DEBUG = false;
    private static boolean GENERATE_NEW_KEYS = false;
    private static String configFile;


    public static void main(String[] args) throws BlockChainException, IOException, NoSuchAlgorithmException {
        if (!correctNumberArgs(args)) throw new BlockChainException(INCORRECT_INITIATOR_ARGUMENTS);
        parseArgs(args);
        if (DEBUG) System.out.println("Debug mode is on");
        if (GENERATE_NEW_KEYS) System.out.println("Generating new keys...");
        config.setFromRelativePath(configFile);
        initProcesses();
    }

    private static void initProcesses() throws BlockChainException {
        try {
            initProcessArray(config.getMemberIds(), memberModule, Member.TYPE);
            initProcessArray(config.getClientIds(), clientModule, Client.TYPE);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new BlockChainException(COULD_NOT_INIT_PROCESS, e.getMessage());
        }

    }

    private static void initProcessArray(ArrayList<Integer> ids, String directory, String processType) throws IOException, NoSuchAlgorithmException {
        String debug = DEBUG ? DEBUG_FLAG : "";
        for (int id : ids) {
            if (GENERATE_NEW_KEYS) generateKeys(directory, KeyFilename.get(processType, id));
            Console console = new MavenConsole(String.valueOf(id), config.getFilePath(), debug);
            console.setDirectory(directory);
            console.setTitle(processType + " " + id);
            console.launch();
        }
    }

    private static void generateKeys(String moduleDirectory, String fileName) throws NoSuchAlgorithmException, IOException {
        KeyPair keys = KeyGenerate.generateRSAkeys();
        String targetKeysDirectory = new ModulePath(moduleDirectory)
                .append("src")
                .append("main")
                .append("resources")
                .append(fileName)
                .getPath();

        RSAKeyWriter.writeToFile(keys, targetKeysDirectory);
    }

    private static boolean correctNumberArgs(String[] args) {
        return args.length >= 1 && args.length <= 3;
    }

    private static void parseArgs(String[] args) {
        String arguments = String.join(" ", args);
        Matcher commandMatcher = ARGUMENTS.matcher(arguments);
        if (commandMatcher.matches()) {
            configFile =        commandMatcher.group("configFile");
            GENERATE_NEW_KEYS = commandMatcher.group("generate") != null &&
                    commandMatcher.group("generate").strip().equals(GENERATE_FLAG);
            DEBUG =             commandMatcher.group("debug") != null &&
                    commandMatcher.group("debug").strip().equals(DEBUG_FLAG);
        }
    }

}
