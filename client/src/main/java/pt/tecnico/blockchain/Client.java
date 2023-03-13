package pt.tecnico.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;


import pt.tecnico.blockchain.Messages.*;

import static pt.tecnico.blockchain.ErrorMessage.*;

import pt.tecnico.blockchain.Config.BlockchainConfig;
import pt.tecnico.blockchain.Keys.KeyFilename;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Path.BlockchainPaths;

import pt.tecnico.blockchain.SlotTimer.*;

public class Client
{
    public static final String TYPE = "Client";

    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static int id;
    private static int port;
    private static String hostname;
    private static boolean DEBUG = false;
    private static final String DEBUG_STRING = "-debug";
    private static RSAKeyStoreById store;
    private static BlockchainConfig config;


    public static void main( String[] args ) throws SocketException, UnknownHostException, InterruptedException {

        if (!correctNumberArgs(args)) throw new BlockChainException(INVALID_MEMBER_ARGUMENTS);
        parseArgs(args);
        if (DEBUG) logger.info("Debug mode on");

        try {
            config = new BlockchainConfig();
            config.setFromAbsolutePath(args[1]);
            setHostnameFromConfig();

            if (DEBUG) printInfo();

            initKeyStore();
            //DatagramSocket clientSocket =  new DatagramSocket(5001);
            DatagramSocket clientSocket = new DatagramSocket(5000, InetAddress.getByName("localhost"));


            SlotTimer slotTimer = new SlotTimer(new BlockchainMemberFrontend(), config.getSlotDuration());
            slotTimer.start();
            
            SlotTimer slotTimer = new SlotTimer(new BlockchainMemberFrontend(), config.getSlotDuration());
            slotTimer.start();

            //InitializeLinks
            initializeLinks();

            Thread deliverThread = new Thread(() -> {
                try {
                    while(true){
                        AuthenticatedPerfectLink.deliver(clientSocket);
                    }
                } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            });

            Thread senderThread = new Thread(() -> {
                try {
                    for (int i =0; i <5;i++)
                    {
                        String message = "Sidnei nao responde";
                        Content content = new BlockChainMessage(message);
                        AuthenticatedPerfectLink.send(clientSocket,content,createKeys.pairClient.getPrivate(),InetAddress.getByName("localhost"),5001);
                    }
                    //Send Message with AuthLink
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            });

            //Start Threads
            deliverThread.start();
            senderThread.start();

            //WAIT
            deliverThread.start();
            senderThread.join();

        } catch (IOException e) {
            throw new BlockChainException(COULD_NOT_LOAD_CONFIG_FILE, e.getMessage());
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

    private static void initializeLinks(){
        AuthenticatedPerfectLink.setId(id);
        PerfectLink.setDeliveredMap(new HashMap<>());
        AuthenticatedPerfectLink.setSource("localhost",5000);
    }
    
    private static void initKeyStore() throws Exception {
        store = new RSAKeyStoreById();
        store.addPrivate(BlockchainPaths.CLIENT_KEYDIR_PATH
                .append(KeyFilename.getWithPrivExtension(TYPE, id))
                .getPath());
        store.addPublics(BlockchainPaths.MEMBER_KEYDIR_PATH.getPath());
    }

    private static void parseArgs(String[] args) {
        id = Integer.parseInt(args[0]);
        setDebugMode(args);
    }

    private static void setHostnameFromConfig() {
        Pair<String, Integer> host = config.getClientHostname(id);
        if (host == null) throw new BlockChainException(MEMBER_DOES_NOT_EXIST, id);
        hostname = host.getFirst();
        port = host.getSecond();

    }
}
