package pt.tecnico.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import static pt.tecnico.blockchain.ErrorMessage.*;
import pt.tecnico.blockchain.SlotTimer.*;
import pt.tecnico.blockchain.Messages.*;

public class Member
{
    public static final String MODULE = "blockchain-member";
    public static final String TYPE = "Member";

    private static final Logger logger = LoggerFactory.getLogger(Member.class);
    private static int id;
    private static int port;
    private static String hostname;
    private static boolean DEBUG = false;
    private static final String DEBUG_STRING = "-debug";

    public static void main( String[] args ) throws UnknownHostException, SocketException, InterruptedException {
        {
            for (String arg : args) {
                logger.info(arg);
            }
            logger.info("Debug mode on");
        }

        if (!correctNumberArgs(args)) throw new BlockChainException(INVALID_MEMBER_ARGUMENTS);
        setDebugMode(args);

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

        SlotTimer slotTimer = new SlotTimer(new MemberFrontend(), config.getSlotDuration());
        slotTimer.start();

        DatagramSocket memberSocket = new DatagramSocket(5001, InetAddress.getByName("localhost"));


        //InitializeLinks
        initializeLinks();

        Thread deliverThread = new Thread(() -> {
            try {
                while(true){
                    AuthenticatedPerfectLink.deliver(memberSocket);
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
                    AuthenticatedPerfectLink.send(memberSocket,content,createKeys.pairMember.getPrivate(),InetAddress.getByName("localhost"),5001);
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
        AuthenticatedPerfectLink.setSource("localhost",5001);

    }
}
