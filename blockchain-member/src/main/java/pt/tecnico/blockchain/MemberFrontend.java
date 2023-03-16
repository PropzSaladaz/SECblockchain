package pt.tecnico.blockchain;

import pt.tecnico.blockchain.SlotTimer.*;
import pt.tecnico.blockchain.Messages.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TimerTask;

public class MemberFrontend implements Callable {

    static ArrayList<Pair<String, Integer>> _memberHostNames;
    static ArrayList<Pair<String, Integer>> _clientHostNames;
    static DatagramSocket _socket;

    public TimerTask getTask() {
        return new TimerTask() {
            @Override
            public void run() {
                performArbitraryBehaviour();
            }
        };
    }

    public static void performArbitraryBehaviour() {

    }

    public static void broadcastMessage(Content message) throws IOException, NoSuchAlgorithmException {
        for (Pair<String, Integer> pair : _memberHostNames ){
            AuthenticatedPerfectLink.send(_socket, message, pair.getFirst(), pair.getSecond());
        }
    }

    public static void broadcastClients(Content message) throws IOException, NoSuchAlgorithmException {
        for (Pair<String, Integer> pair : _clientHostNames ){
            AuthenticatedPerfectLink.send(_socket, message, pair.getFirst(), pair.getSecond());

        }
    }

    public static void setFrontEnd(DatagramSocket socket, ArrayList<Pair<String, Integer>> memberHostNames,ArrayList<Pair<String, Integer>> clientHostNames){
        _memberHostNames = memberHostNames;
        _clientHostNames = clientHostNames;
        _socket = socket;
    }
}
