package pt.tecnico.blockchain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static pt.tecnico.blockchain.ErrorMessage.*;


public class Initiator {
    private static String DEBUG_STRING = "-debug";
    private static boolean DEBUG = false;
    private static String filepath;

    public static void main( String[] args ) throws Exception {
        if (!correctNumberArgs(args)) throw new BlockChainException(INCORRECT_INITIATOR_ARGUMENTS);
        if (debugModeOn(args[1])) { DEBUG = true; }
        setFilepath(args);
        parseFile();
    }

    private static void parseFile() throws BlockChainException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                parseLine(line);
            }
        } catch (IOException e) {
            throw new BlockChainException(WRONG_FILE_FORMAT);
        }
    }

    private static void parseLine(String line) {

    }

    private static boolean correctNumberArgs(String[] args) {
        return args.length >= 2 && args.length <= 3;
    }

    private static boolean debugModeOn(String mode) {
        return mode.equals(DEBUG_STRING);
    }

    private static void setFilepath(String[] args) {
            filepath = DEBUG ? args[2] : args[1];
    }
}
