package pt.tecnico.blockchain;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static pt.tecnico.blockchain.ErrorMessage.*;
import static pt.tecnico.blockchain.Path.BlockchainPaths.CLIENT_KEYDIR_PATH;
import static pt.tecnico.blockchain.Path.BlockchainPaths.MEMBER_KEYDIR_PATH;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Keys.KeyFilename;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.client.BlockchainClientAPI;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;

public class Client
{
    public static final String TYPE = "Client";

    private static int pid;
    private static int port;
    private static String hostname;
    private static boolean DEBUG = false;
    private static final String DEBUG_STRING = "-debug";
    private static BlockchainConfig config;


    public static void main( String[] args ) {

        if (!correctNumberArgs(args)) throw new BlockChainException(INVALID_PROCESS_ARGUMENTS);
        parseArgs(args);

        try {
            config = new BlockchainConfig();
            config.setFromAbsolutePath(args[1]);
            setHostnameFromConfig();

            if (DEBUG) {
                Logger.setDebug();
                Logger.logDebug(getProcessInfo());
            }

            initKeyStore();
            initializeLinks();

            BlockchainClientAPI.setMembers(config.getMemberHostnames());

            DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName(hostname));
            TESClientAPI client = new TESClientAPI(socket,
                    RSAKeyStoreById.getPublicKey(pid), RSAKeyStoreById.getPrivateKey(pid));
            RequestScheduler.setClient(client);

            Thread.sleep(config.timeUntilStart());

            client.waitForMessages();
            RequestScheduler.startFromConfig(pid, config);



        } catch (SocketException e) {
            System.out.println("Could not create socket!");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            e.printStackTrace();
        } catch (IOException e) {
            throw new BlockChainException(COULD_NOT_LOAD_CONFIG_FILE);
        } catch (InterruptedException e) {
            System.out.println("Thread error");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean correctNumberArgs(String[] args) {
        return args.length >= 2 && args.length <= 3;
    }

    private static void setDebugMode(String[] args) {
        if (args.length == 3 && args[2].equals(DEBUG_STRING))
            DEBUG = true;
    }

    private static String getProcessInfo() {
        return "ID=" + pid + "\n" +
                "hostname=" + hostname + "\n" +
                "port=" + port + "\n";
    }

    private static void initializeLinks() throws UnknownHostException {
        AuthenticatedPerfectLink.setSource(hostname, port);
        AuthenticatedPerfectLink.setId(pid);
    }
    
    private static void initKeyStore() throws Exception {
        RSAKeyStoreById.addPrivate(CLIENT_KEYDIR_PATH
                .append(KeyFilename.getWithPrivExtension(TYPE, pid))
                .getPath());
        RSAKeyStoreById.addPublic(CLIENT_KEYDIR_PATH
                .append(KeyFilename.getWithPubExtension(TYPE, pid))
                .getPath());
        RSAKeyStoreById.addPublics(MEMBER_KEYDIR_PATH.getPath());
    }

    private static void parseArgs(String[] args) {
        pid = Integer.parseInt(args[0]);
        setDebugMode(args);
    }

    private static void setHostnameFromConfig() {
        Pair<String, Integer> host = config.getClientHostname(pid);
        if (host == null) throw new BlockChainException(CLIENT_DOES_NOT_EXIST, pid);
        hostname = host.getFirst();
        port = host.getSecond();

    }
}
