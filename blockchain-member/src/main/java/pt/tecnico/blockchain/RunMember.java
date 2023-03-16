package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.links.APLMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;

public class RunMember {

    public static void run(DatagramSocket socket, MemberState memberState) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {

           Thread worker = new Thread(() -> {
                  try {
                      while (true) {
                          APLMessage message = (APLMessage) AuthenticatedPerfectLink.deliver(socket);
                          MemberServicesImpl.handleRequest((ApplicationMessage) message.getContent(), memberState);
                      }
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
           });
           worker.start();

    }
}

