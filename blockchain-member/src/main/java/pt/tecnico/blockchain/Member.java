package pt.tecnico.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static pt.tecnico.blockchain.ErrorMessage.*;

public class Member
{
    private static final Logger logger = LoggerFactory.getLogger(Member.class);
    private static int id;
    private static int port;
    private static String hostname;
    private static boolean DEBUG = false;
    private static final String DEBUG_STRING = "-debug";

    public static void main( String[] args )
    {
        if (!correctNumberArgs(args)) throw new BlockChainException(INVALID_MEMBER_ARGUMENTS);
        setDebugMode(args);
        if (DEBUG) logger.info("Debug mode on");

        id = Integer.parseInt(args[0]);
        BlockchainConfig config;

        try {
            config = new BlockchainConfig();
            config.setFromAbsolutePath(args[1]);
        } catch (IOException e) {
            throw new BlockChainException(COULD_NOT_LOAD_CONFIG_FILE, e.getMessage());
        }

        Pair<String, Integer> host = config.getMemberHostname(id);
        if (host == null) throw new BlockChainException(MEMBER_DOES_NOT_EXIST, id);

        hostname = host.getFirst();
        port = host.getSecond();

        if (DEBUG) printInfo();

        // TODO All member work is to be done

    }

    private static boolean correctNumberArgs(String[] args) {
        return args.length >= 2 && args.length <= 3;
    }

    private static void setDebugMode(String[] args) {
        if (args.length == 3 && args[2].equals(DEBUG_STRING))
            DEBUG = true;
    }

    private static void printInfo() {
        logger.info("ID=" + id + "\n" +
                "hostname=" + hostname + "\n" +
                "port=" + port + "\n");
    }
}
