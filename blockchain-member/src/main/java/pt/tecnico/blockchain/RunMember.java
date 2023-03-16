package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Messages.ApplicationMessage;
import pt.tecnico.blockchain.Messages.Content;
import pt.tecnico.blockchain.Messages.links.APLMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;

public class RunMember {

    public static void run(DatagramSocket socket) throws IOException {

           Thread worker = new Thread(() -> {
                  try {
                      while (true) {
                          Content message =  AuthenticatedPerfectLink.deliver(socket);
                          System.out.println("Before sending");
                          MemberServicesImpl.handleRequest(message);
                          System.out.println("After sending");
                      }
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
           });
           worker.start();

    }
}

