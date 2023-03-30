package pt.tecnico.blockchain.links;

import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

import pt.tecnico.blockchain.Messages.*;
import pt.tecnico.blockchain.Messages.links.PLMessage;
import pt.tecnico.blockchain.Pair;
import pt.tecnico.blockchain.SlotTimer.ScheduledTask;
import pt.tecnico.blockchain.behavior.LinkBehaviorController;

public class PerfectLink {
    public static final int RESEND_MESSAGE_TIMEOUT = 5000;

    private static final ConcurrentHashMap<Pair<InetAddress, Integer>, Integer> _ackSeqNum = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Pair<InetAddress, Integer>, Integer> _deliveredSeqNum = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ScheduledTask> _stubbornTasks = new ConcurrentHashMap<>();
    private static InetAddress _address;
    private static int _port;

    public static void send(DatagramSocket socket, Content content, InetAddress hostname, int port) {
        LinkBehaviorController.PLsend(socket, content, hostname, port);
    }

    public static Content deliver(DatagramSocket socket) throws IOException, ClassNotFoundException{
        return LinkBehaviorController.PLdeliver(socket);
    }

    public static Integer getDeliveredSeqNum(Pair<InetAddress,Integer> hostInfo) {
        Integer seqNum = _deliveredSeqNum.get(hostInfo);
        if (seqNum == null) {
            _deliveredSeqNum.put(hostInfo, 0);
            seqNum = 0;
        }
        return seqNum;
    }

    public static Integer getAckSeqNum(Pair<InetAddress,Integer> hostInfo) {
        Integer seqNum = _ackSeqNum.get(hostInfo);
        if (seqNum == null) {
            _ackSeqNum.put(hostInfo, 0);
            seqNum = 0;
        }
        return seqNum;
    }

    public static void addToStubbornTasks(String key, ScheduledTask task) {
        _stubbornTasks.put(key, task);
    }

    public static void stopStubbornTask(String key) {
        ScheduledTask task = _stubbornTasks.get(key);
        if (task == null) {
            System.out.println("ERROR: Trying to stop a stubborn task that does not exist.");
        } else {
            _stubbornTasks.get(key).stop();
            _stubbornTasks.remove(key);
        }
    }

    public static void incrAckSeqNum(Pair<InetAddress,Integer> hostInfo) {
        _ackSeqNum.put(hostInfo, _ackSeqNum.get(hostInfo)+1);
    }

    public static void incrDeliveredSeqNum(Pair<InetAddress,Integer> hostInfo) {
        _deliveredSeqNum.put(hostInfo, _deliveredSeqNum.get(hostInfo)+1);
    }

    public static void sendAck(DatagramSocket socket, PLMessage message) {
        PLMessage ackMessage = new PLMessage(_address, _port, message.getContent());
        ackMessage.setSeqNum(message.getSeqNum());
        ackMessage.setAck(true);
        FairLossLink.send(socket, ackMessage, message.getSenderHostname(), message.getSenderPort());
    }

    public static void setSource(String address, int port) throws UnknownHostException {
        _address = InetAddress.getByName(address);
        _port = port;
    }

    public static InetAddress getAddress() {
        return _address;
    }

    public static int getPort() {
        return _port;
    }
}
