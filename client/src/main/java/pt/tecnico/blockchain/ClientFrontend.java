package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.SlotTimer.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TimerTask;

public class ClientFrontend implements Callable {
    static ArrayList<Pair<String, Integer>> _memberHostNames;
    static DatagramSocket _socket;

    public TimerTask getTask() {
        return new TimerTask() {
            @Override
            public void run() {
            }
        };
    }

    public static void broadcastClientRequests(Content message) throws IOException, NoSuchAlgorithmException {
        for (Pair<String, Integer> pair : _memberHostNames ){
            AuthenticatedPerfectLink.send(_socket, message, pair.getFirst(), pair.getSecond());
        }
    }

    public static void setFrontEnd(DatagramSocket socket, ArrayList<Pair<String, Integer>> memberHostNames){
        _memberHostNames = memberHostNames;
        _socket = socket;
    }
}
