package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Keys.KeyFilename;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Path.BlockchainPaths;
import pt.tecnico.blockchain.contracts.tes.TESContract;
import pt.tecnico.blockchain.links.AuthenticatedPerfectLink;
import pt.tecnico.blockchain.server.BlockchainMemberAPI;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import static pt.tecnico.blockchain.ErrorMessage.*;

public class Member
{
    public static final String TYPE = "Member";
    private static final String DEBUG_STRING = "-debug";
    private static int id;
    private static int port;
    private static String hostname;
    private static boolean DEBUG = false;
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
            MemberSlotBehavior behavior = new MemberSlotBehavior(config, id);

            DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName(hostname));
            BlockchainMemberAPI blockchainMemberAPI = new BlockchainMemberAPI(socket, config.getClients(), RSAKeyStoreById.getPublicFromPid(id));
            TESContract contract = new TESContract(id);
            contract.setMiners(Collections.singletonList(Crypto.getHashFromKey(RSAKeyStoreById.getPublicKey(1))));
            blockchainMemberAPI.addContractToBlockchain(contract);

            MemberServicesImpl.init(config.getClientHostnames(), blockchainMemberAPI);
            Ibft.init(socket, id, config.getMemberHostnames(), blockchainMemberAPI);

            Thread.sleep(config.timeUntilStart());

            behavior.track();
            RunMember.run(socket);

        } catch (IOException e) {
            throw new BlockChainException(COULD_NOT_LOAD_CONFIG_FILE, e.getMessage());
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
        return "ID=" + id + "\n" +
                "hostname=" + hostname + "\n" +
                "port=" + port + "\n";
    }

    private static void initializeLinks() throws UnknownHostException {
        AuthenticatedPerfectLink.setSource(hostname, port);
        AuthenticatedPerfectLink.setId(id);
    }

    private static void initKeyStore() throws Exception {
        RSAKeyStoreById.addPrivate(BlockchainPaths.MEMBER_KEYDIR_PATH
                .append(KeyFilename.getWithPrivExtension(TYPE, id))
                .getPath());
        RSAKeyStoreById.addPublics(BlockchainPaths.CLIENT_KEYDIR_PATH.getPath());
        RSAKeyStoreById.addPublics(BlockchainPaths.MEMBER_KEYDIR_PATH.getPath());
    }

    private static void parseArgs(String[] args) {
        id = Integer.parseInt(args[0]);
        setDebugMode(args);
    }

    private static void setHostnameFromConfig() {
        Pair<String, Integer> host = config.getMemberHostname(id);
        if (host == null) throw new BlockChainException(MEMBER_DOES_NOT_EXIST, id);
        hostname = host.getFirst();
        port = host.getSecond();

    }

    public static int getMyId() {
        return id;
    }
}
