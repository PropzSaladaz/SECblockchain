package pt.tecnico.blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AuthenticatedPerfectLink implements ILink{

    Map<Integer, PublicKey> _publicKeys;
    PerfectLink _perfectLink;
    PrivateKey _privateKey;

    public AuthenticatedPerfectLink(Map<Integer, PublicKey> publicKeys, Set<UUID> set, PrivateKey privateKey){
        _publicKeys = publicKeys;
        _perfectLink = new PerfectLink(set);
        _privateKey = privateKey;
    }


    public UdpMessage send(DatagramSocket socket, UdpMessage udpMessage, String address, int remotePort) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //Extract Variables from Message
        String message = udpMessage.getMessage();
        UUID uuid = udpMessage.getUUID();
        int processId =udpMessage.getProcessId();
        //Create Digital Signature
        byte[] encryptedMessage = Crypto.authenticate(_privateKey,uuid,message, processId);
        udpMessage.setEncryptMac(encryptedMessage);
        //Send Message
        _perfectLink.send(socket,udpMessage,address,remotePort);
        return udpMessage;

    }

    public UdpMessage deliver(DatagramSocket socket) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        //Start waiting Delivery
        Map<String, UdpMessage> deliverMap= _perfectLink.deliverAuthLink(socket,packet,buffer);
        UdpMessage udpMessage;
        //Check it was already delivered
        if(deliverMap.containsKey("YES")){
            //Extract Variables from Message
            udpMessage = deliverMap.get("YES");
            String message = udpMessage.getMessage();
            UUID uuid = udpMessage.getUUID();
            int processId =udpMessage.getProcessId();
            byte[] encryptedMac = udpMessage.getEncryptedMac();
            //Verify Digital Signature
            if(_publicKeys.containsKey(processId) && Crypto.verifyAuth(_publicKeys.get(processId),encryptedMac,uuid,message,processId)){
                //TO DO DELIVER
                System.out.println("Receiver-> Auth: Veryfied | Message: " + udpMessage.getMessage()+ "\n");
                return udpMessage;
            }else{
                //TO DO NOT DELIVER
                System.out.println("Receiver-> Auth: Not Veryfied | Message: " + udpMessage.getMessage()+ "\n");
            }

        }
        udpMessage = deliverMap.get("NO");
        return udpMessage;
    }


}
