package pt.tecnico.blockchain;

import java.util.Map;


public class BlockchainConfig
{
    private static String COMMAND_PATTERN = "^(?<command>[PART])";
    private static String PROCESS_PATTERN = "^(?<command>P) (?<process>\\d+) (?<process_type>[MLC]) (?<hostname>\\w+):(?<port>\\d+)$";
    private static String SLOT_PATTERN = "^(?<command>T) (?<duration>\\d+)";
    private static String OPERATION_PATTERN = "^(?<operation_type>A) (?<slot>\\d+) (?<operation>((?<process_id>\\d+))+";

    private Map<Integer, String> members;
    private Map<Integer, String> clients;
    private Map<Integer, String> operations;
    private Map<Integer, String> requests;
    private int slotDuration;

    public BlockchainConfig(String inputFile) {

    }
}
