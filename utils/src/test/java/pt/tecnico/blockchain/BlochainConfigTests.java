package pt.tecnico.blockchain;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.tecnico.blockchain.Config.BlockchainConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for simple App.
 */
public class BlochainConfigTests
{
    private static String filename = "example.txt";
    private static BlockchainConfig config;
    /**
     * Rigorous Test :-)
     */
    @BeforeClass
    public static void createFile()
    {
        try {
            File file = new File(filename);
            FileWriter writer = new FileWriter(file, false);
            writer.write(
                    "P 1 M 127.0.0.1:10001\n" +
                    "P 2 M 127.0.0.1:10002\n" +
                    "P 3 M 127.0.0.1:10003\n" +
                    "P 4 M 127.0.0.1:10004\n" +
                    "P 5 C 127.0.0.1:10005\n" +
                    "T 500\n" +
                    "A 2 (1, O) (2, C) (4, A, 3)\n" +
                    "R 2 (5, \"balelas-string\", 10) (6, \"popota\", 300)\n"
            );
            writer.close();
            config = new BlockchainConfig();
            config.setFromAbsolutePath(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSlotDuration() {
        int duration = config.getSlotDuration();
        assertEquals(duration, 500);
    }

    @Test
    public void getMembers() {
        ArrayList<Pair<String, Integer>> members = config.getMemberHostnames();
        assertEquals(members.size(), 4);
    }

    @Test
    public void getClients() {
        ArrayList<Pair<String, Integer>> members = config.getClientHostnames();
        assertEquals(members.size(), 1);
    }

    @Test
    public void getMemberIds() {
        ArrayList<Integer> ids = config.getMemberIds();

        assertEquals(ids.size(), 4);

        int i = 1;
        for (int id : ids) {
            assertEquals(id, i);
            i++;
        }
    }

    @Test
    public void getClientIds() {
        ArrayList<Integer> ids = config.getClientIds();

        assertEquals(ids.size(), 1);

        int i = 5;
        for (int id : ids) {
            assertEquals(id, i);
            i++;
        }
    }

    @Test
    public void getMember() {
        Pair<String, Integer> m1 = config.getMemberHostname(1);
        Pair<String, Integer> m4 = config.getMemberHostname(4);
        Pair<String, Integer> m5 = config.getMemberHostname(5);

        assertEquals(m1.getFirst(), "127.0.0.1");
        assertEquals((int)m1.getSecond(), 10001);

        assertEquals(m4.getFirst(), "127.0.0.1");
        assertEquals((int)m4.getSecond(), 10004);

        assertNull(m5);
    }

    @Test
    public void getClient() {
        Pair<String, Integer> c5 = config.getClientHostname(5);
        Pair<String, Integer> c6 = config.getClientHostname(6);

        assertEquals(c5.getFirst(), "127.0.0.1");
        assertEquals((int)c5.getSecond(), 10005);

        assertNull(c6);
    }

    @Test
    public void getRequestInSlotForProcess() {
        Pair<String, Integer> request1 = config.getRequestInSlotForProcess(2, 5);
        Pair<String, Integer> request2 = config.getRequestInSlotForProcess(2, 6);

        assertEquals(request1.getFirst(), "balelas-string");
        assertEquals((int)request1.getSecond(), 10);

        assertEquals(request2.getFirst(), "popota");
        assertEquals((int)request2.getSecond(), 300);
    }

    @Test
    public void getOperationInSlotForProcess() {
        Pair<String, Integer> op1 = config.getOperationInSlotForProcess(2, 1);
        Pair<String, Integer> op2 = config.getOperationInSlotForProcess(2, 2);
        Pair<String, Integer> op3 = config.getOperationInSlotForProcess(2, 4);

        assertEquals(op1.getFirst(), "O");
        assertEquals((int)op1.getSecond(), -1);

        assertEquals(op2.getFirst(), "C");
        assertEquals((int)op2.getSecond(), -1);

        assertEquals(op3.getFirst(), "A");
        assertEquals((int)op3.getSecond(), 3);
    }

    @AfterClass
    public static void deleteFile() {
        File file = new File(filename);
        file.delete();
    }
}
