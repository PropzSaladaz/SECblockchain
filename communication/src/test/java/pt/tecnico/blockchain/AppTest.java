package pt.tecnico.blockchain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import pt.tecnico.blockchain.*;

import org.junit.Test;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testUdpString() throws InterruptedException {
        Thread receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    IReceiver receiver = new UdpReceiver(5001);
                    receiver.receiveMessages();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread senderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ISender sender = new UdpSender("localhost", 5000);
                    sender.sendMessage("Sender: Sidnei nao responde", "localhost", 5001);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        receiverThread.start();
        senderThread.start();

        //WAIT
        receiverThread.join();
        senderThread.join();
    }

}
