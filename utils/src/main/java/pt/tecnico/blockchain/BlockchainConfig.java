package pt.tecnico.blockchain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pt.tecnico.blockchain.Pair;

import static pt.tecnico.blockchain.ErrorMessage.*;


public class BlockchainConfig
{
    private final String CONFIG_PATH = File.separator + "blockchain-initiator";

    private final Pattern COMMAND_PATTERN = Pattern.compile("^(?<command>[PART])");
    private final Pattern CREATE_PROCESS_PATTERN = Pattern.compile("^P (?<processId>\\d+)" +
            " (?<processType>[MC])" +
            " (?<hostname>[\\w\\d.-\\/]+):(?<port>\\d+)$");
    private final Pattern SLOT_DELAY_PATTERN = Pattern.compile("^T (?<duration>\\d+)$");
    private final Pattern ARBITRARY_BEHAVIOR_PATTERN = Pattern.compile("^A (?<slot>\\d+)" +
            "(?<operation>( \\(\\d+, [OCA](, \\d+)?\\))+)$");
    private final Pattern ARBITRARY_COMMAND_INFO_PATTERN = Pattern.compile(" \\((?<processId>\\d+)," +
            " (?<operator>[OCA])" +
            "(, (?<authenticateAs>\\d+))?\\)");
    private final Pattern CLIENT_REQUEST_PATTERN = Pattern.compile("^R\\s(?<slot>\\d+)" +
            "(?<request>( \\(\\d+, \\\"[^\\\"]+\\\", \\d+\\))+)$");
    private final Pattern CLIENT_REQUEST_INFO_PATTERN = Pattern.compile("( \\((?<clientId>\\d+)," +
            " \\\"(?<message>[^\\\"]+)\\\"" +
            ", (?<messageDelay>\\d+)\\))");

    // Commands
    private final String CREATE_PROCESS = "P";
    private final String SLOT_DELAY = "T";
    private final String ARBITRARY_BEHAVIOR = "A";
    private final String CLIENT_REQUEST = "R";

    // Process types
    private final String MEMBER = "M";
    private final String CLIENT = "C";

    // Members' behavior operators
    private final String OMIT_MESSAGES = "O";
    private final String CORRUPT_MESSAGES = "C";
    private final String AUTHENTICATE_AS = "A";
    private final Set<String> setOfBehaviorOperators = new HashSet<>(Arrays.asList(
            OMIT_MESSAGES, CORRUPT_MESSAGES, AUTHENTICATE_AS));

    private Map<Integer, Pair<String, Integer>> members = new HashMap<>();
    private Map<Integer, Pair<String, Integer>> clients = new HashMap<>();
    private Map<Integer, Map<Integer, Pair<String, Integer>>> operations = new HashMap<>();
    private Map<Integer, Map<Integer, Pair<String, Integer>>> requests = new HashMap<>();
    private int slotDuration;
    private String filePath;

    public BlockchainConfig() {

    }

    public void setFromAbsolutePath(String file) throws IOException {
        filePath = file;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                parseLine(line);
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void setFromRelativePath(String file) throws IOException {
        Path currentPath = Paths.get(new File("").getAbsolutePath());
        filePath = currentPath.getParent().toString() + CONFIG_PATH + File.separator + file;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                parseLine(line);
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }


    public Pair<String, Integer> getOperationInSlotForProcess(int slot, int processId) {
        return operations.get(slot).get(processId);
    }

    public Pair<String, Integer> getRequestInSlotForProcess(int slot, int processId) {
        return requests.get(slot).get(processId);
    }

    public ArrayList<Integer> getMemberIds() {
        return new ArrayList<>(members.keySet());
    }

    public ArrayList<Integer> getClientIds() {
        return new ArrayList<>(clients.keySet());
    }

    public ArrayList<Pair<String, Integer>> getMemberHostnames() {
        return new ArrayList<>(members.values());
    }

    public ArrayList<Pair<String, Integer>> getClientHostnames() {
        return new ArrayList<>(clients.values());
    }

    public Pair<String, Integer> getMemberHostname(int id) {
        return members.get(id);
    }

    public Pair<String, Integer> getClientHostname(int id) {
        return clients.get(id);
    }

    public int getSlotDuration() {
        return slotDuration;
    }

    public String getFilePath() {
        return filePath;
    }


    private void parseLine(String line) throws BlockChainException {
        Matcher commandMatcher = COMMAND_PATTERN.matcher(line);
        if (commandMatcher.find()) {
            String command = commandMatcher.group("command");
            switch (command) {
                case CREATE_PROCESS:
                    parseCreateProcess(line);
                    break;
                case SLOT_DELAY:
                    parseSlotDelay(line);
                    break;
                case ARBITRARY_BEHAVIOR:
                    parseArbitraryBehavior(line);
                    break;
                case CLIENT_REQUEST:
                    parseClientRequest(line);
                    break;
                default:
                    break;
            }
        }
        else {
            throw new BlockChainException(WRONG_FILE_FORMAT);
        }
    }

    private void parseCreateProcess(String line) throws BlockChainException {
        Matcher matcher = CREATE_PROCESS_PATTERN.matcher(line);
        if (matcher.matches()) {
            int processId = Integer.parseInt(matcher.group("processId"));
            String processType = matcher.group("processType");
            String hostname = matcher.group("hostname");
            int port = Integer.parseInt(matcher.group("port"));

            switch (processType) {
                case MEMBER:
                    members.put(processId, new Pair<>(hostname, port));
                    break;
                case CLIENT:
                    clients.put(processId, new Pair<>(hostname, port));
                    break;
                default:
                    throw new BlockChainException(INVALID_PROCESS_TYPE);
            }
        }
        else {
            throw new BlockChainException(WRONG_FILE_FORMAT);
        }
    }

    private void parseSlotDelay(String line) throws BlockChainException {
        Matcher matcher = SLOT_DELAY_PATTERN.matcher(line);
        if (matcher.matches()) {
            slotDuration = Integer.parseInt(matcher.group("duration"));
        }
        else {
            throw new BlockChainException(WRONG_FILE_FORMAT);
        }
    }

    private void parseArbitraryBehavior(String line) throws BlockChainException {
        Matcher matcher = ARBITRARY_BEHAVIOR_PATTERN.matcher(line);
        if (matcher.matches()) {
            int slot = Integer.parseInt(matcher.group("slot"));
            String operationString = matcher.group("operation");

            if (operations.get(slot) == null) operations.put(slot, new HashMap());

            matcher = ARBITRARY_COMMAND_INFO_PATTERN.matcher(operationString);
            // store each individual operation
            while (matcher.find()) {
                int processId = Integer.parseInt(matcher.group("processId"));
                String operator = matcher.group("operator");
                int authenticateAs = -1;

                if (setOfBehaviorOperators.contains(operator)) {
                    // Only authenticate_as command has a second parameter
                    if (operator.equals(AUTHENTICATE_AS)) {
                        authenticateAs = Integer.parseInt(matcher.group("authenticateAs"));
                    }

                    operations.get(slot).put(processId, new Pair<>(operator, authenticateAs));
                }
                else {
                    throw new BlockChainException(INVALID_BEHAVIOR_OPERATOR);
                }
            }
        }
        else {
            throw new BlockChainException(WRONG_FILE_FORMAT);
        }
    }

    private void parseClientRequest(String line) throws BlockChainException {
        Matcher matcher = CLIENT_REQUEST_PATTERN.matcher(line);
        if (matcher.matches()) {
            int slot = Integer.parseInt(matcher.group("slot"));
            String requestString = matcher.group("request");

            if (requests.get(slot) == null) requests.put(slot, new HashMap());

            matcher = CLIENT_REQUEST_INFO_PATTERN.matcher(requestString);
            // store each individual request
            while (matcher.find()) {
                int clientId = Integer.parseInt(matcher.group("clientId"));
                String message = matcher.group("message");
                int messageDelay = Integer.parseInt(matcher.group("messageDelay"));

                requests.get(slot).put(clientId, new Pair<>(message, messageDelay));
            }

        }
        else {
            throw new BlockChainException(WRONG_FILE_FORMAT);
        }
    }
}
