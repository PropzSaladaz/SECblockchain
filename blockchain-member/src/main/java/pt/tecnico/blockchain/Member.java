package pt.tecnico.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static pt.tecnico.blockchain.ErrorMessage.*;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Keys.KeyFilename;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Messages.blockchain.BlockchainMessage;
import pt.tecnico.blockchain.Path.BlockchainPaths;
import pt.tecnico.blockchain.SlotTimer.*;
import pt.tecnico.blockchain.Messages.*;

public class Member
{
    public static final String TYPE = "Member";
    private static final String DEBUG_STRING = "-debug";

    private static final Logger logger = LoggerFactory.getLogger(Member.class);
    private static int id;
    private static int port;
    private static String hostname;
    private static boolean DEBUG = false;
    private static RSAKeyStoreById store;
    private static BlockchainConfig config;


    public static void main( String[] args ) {
        if (!correctNumberArgs(args)) throw new BlockChainException(INVALID_PROCESS_ARGUMENTS);
        parseArgs(args);

        try {
            config = new BlockchainConfig();
            config.setFromAbsolutePath(args[1]);
            setHostnameFromConfig();

            if (DEBUG) printInfo();

            initKeyStore();
            MemberState memberState = new MemberState(config);
            DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName(hostname));
            initializeLinks();
            
//            while (true) {
//                APLMessage message = (APLMessage) AuthenticatedPerfectLink.deliver(socket);
//                Thread worker = new Thread(() -> {
//                    try {
//                        MemberServicesImpl.handleRequest((ApplicationMessage) message.getContent(), memberState);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//            }


            Thread deliverThread = new Thread(() -> {
                try {
                    while(true){
                        AuthenticatedPerfectLink.deliver(socket);
                    }
                } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            });

           Thread senderThread = new Thread(() -> {
                try {
                    for (int i =0; i < 10 ; i++)
                    {
                        if (id == 1 || id == 3) {
                            String message = "Sidnei nao responde " + i;
                            Content content = new BlockchainMessage(message);
                            AuthenticatedPerfectLink.send(socket, content, InetAddress.getByName("127.0.0.1"),10002);
                        }
                    }
                    //Send Message with AuthLink
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            });

            deliverThread.start();
            senderThread.start();

            deliverThread.join();
            senderThread.join();

            
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

    private static void printInfo() {
        logger.info("ID=" + id + "\n" +
                "hostname=" + hostname + "\n" +
                "port=" + port + "\n");
    }


    private static void initializeLinks() throws UnknownHostException {
        AuthenticatedPerfectLink.setSource(hostname, port);
        AuthenticatedPerfectLink.setKeyStore(store);
        AuthenticatedPerfectLink.setId(id);
    }

    private static void initKeyStore() throws Exception {
        store = new RSAKeyStoreById();
        store.addPrivate(BlockchainPaths.MEMBER_KEYDIR_PATH
                .append(KeyFilename.getWithPrivExtension(TYPE, id))
                .getPath());
        store.addPublics(BlockchainPaths.CLIENT_KEYDIR_PATH.getPath());
        store.addPublics(BlockchainPaths.MEMBER_KEYDIR_PATH.getPath());
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
}
