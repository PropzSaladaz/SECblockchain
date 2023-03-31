package pt.tecnico.blockchain.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.tecnico.blockchain.BlockChainException;
import pt.tecnico.blockchain.Config.operations.CheckBalanceOperation;
import pt.tecnico.blockchain.Config.operations.ClientOperation;
import pt.tecnico.blockchain.Config.operations.CreateAccountOperation;
import pt.tecnico.blockchain.Config.operations.TransferOperation;
import pt.tecnico.blockchain.Pair;

import static pt.tecnico.blockchain.ErrorMessage.*;


public class BlockchainConfig
{
    private final String CONFIG_PATH = File.separator + "blockchain-initiator";

    private final Pattern COMMAND_PATTERN = Pattern.compile("^(?<command>[PARTS])");
    private final Pattern START_TIME_PATTERN = Pattern.compile("^S (?<hours>\\d{2}):(?<minutes>\\d{2}):(?<seconds>\\d{2})$");
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
            "(?<request>( \\(\\d+, [CBT](\\(\\d+, \\d+\\))?, \\d+\\))+)$");
    private final Pattern CLIENT_REQUEST_INFO_PATTERN = Pattern.compile(" \\((?<clientId>\\d+), " +
            "(?<operation>[CBT])(?<arguments>\\(\\d+, \\d+\\))?, " +
            "(?<gasPrice>\\d+), " +
            "(?<gasLimit>\\d+)\\)");

    // Commands
    private final String START_TIME = "S";
    private final String CREATE_PROCESS = "P";
    private final String SLOT_DELAY = "T";
    private final String ARBITRARY_BEHAVIOR = "A";
    private final String CLIENT_REQUEST = "R";

    // Process types
    private final String MEMBER = "M";
    private final String CLIENT = "C";

    // Members' behavior operators
    public static final String OMIT_MESSAGES = "O";
    public static final String CORRUPT_MESSAGES = "C";
    public static final String AUTHENTICATE_AS = "A";

    // Client operations
    public static final String CREATE_ACCOUNT = "C";
    public static final String TRANSFER = "T";
    public static final String CHECK_BALANCE = "B";

    private final Set<String> setOfBehaviorOperators = new HashSet<>(Arrays.asList(
            OMIT_MESSAGES, CORRUPT_MESSAGES, AUTHENTICATE_AS));

    private Map<Integer, Pair<String, Integer>> members = new HashMap<>();
    private Map<Integer, Pair<String, Integer>> clients = new HashMap<>();
    private Map<Integer, Map<Integer, Pair<String, Integer>>> behaviors = new HashMap<>();
    private Map<Integer, Map<Integer, ClientOperation>> requests = new HashMap<>();
    private int slotDuration;
    private long startTime;
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


    public Pair<String, Integer> getBehaviorInSlotForProcess(int slot, int processId) {
        if (behaviors.containsKey(slot)) return behaviors.get(slot).get(processId);
        return null;
    }

    public ClientOperation getRequestInSlotForProcess(int slot, int processId) {
        if (requests.containsKey(slot)) return requests.get(slot).get(processId);
        return null;
    }

    public Map<Integer,Pair<String,Integer>> getClients() {
        return clients;
    }

    public ArrayList<Integer> getMemberIds() {
        return new ArrayList<>(members.keySet());
    }

    public ArrayList<Integer> getClientIds() {
        return new ArrayList<>(clients.keySet());
    }

    public int getNumberOfMemberProcesses() {
        return members.size();
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

    public long timeUntilStart() {
        long currentTimeInMillis = System.currentTimeMillis();
        return startTime - currentTimeInMillis;
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
                case START_TIME:
                    parseStartTime(line);
                    break;
                default:
                    break;
            }
        }
        else {
            throw new BlockChainException(WRONG_FILE_FORMAT);
        }
    }

    private void parseStartTime(String line) {
        Matcher matcher = START_TIME_PATTERN.matcher(line);
        if (matcher.matches()) {
            int hours = Integer.parseInt(matcher.group("hours"));
            int minutes = Integer.parseInt(matcher.group("minutes"));
            int seconds = Integer.parseInt(matcher.group("seconds"));
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            LocalDateTime desiredLocalDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), hours, minutes, seconds);
            ZonedDateTime desiredDateTime = ZonedDateTime.ofLocal(desiredLocalDateTime, ZoneId.systemDefault(), ZoneOffset.UTC);
            startTime = desiredDateTime.toInstant().toEpochMilli();
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

            behaviors.computeIfAbsent(slot, k -> new HashMap());

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

                    behaviors.get(slot).put(processId, new Pair<>(operator, authenticateAs));
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

            requests.computeIfAbsent(slot, k -> new HashMap());

            matcher = CLIENT_REQUEST_INFO_PATTERN.matcher(requestString);
            // store each individual request
            while (matcher.find()) {
                int clientId = Integer.parseInt(matcher.group("clientId"));
                String operation = matcher.group("operation");
                int gasPrice = Integer.parseInt(matcher.group("gasPrice"));
                int gasLimit = Integer.parseInt(matcher.group("gasLimit"));

                if (operation != null) {
                    switch(operation) {
                        case TRANSFER:
                            int destination = Integer.parseInt(matcher.group("destination"));
                            int amount = Integer.parseInt(matcher.group("amount"));
                            requests.get(slot).put(clientId, new TransferOperation(destination, amount,
                                    gasPrice, gasLimit));
                            break;
                        case CREATE_ACCOUNT:
                            requests.get(slot).put(clientId, new CreateAccountOperation(gasPrice, gasLimit));
                            break;
                        case CHECK_BALANCE:
                            requests.get(slot).put(clientId, new CheckBalanceOperation(gasPrice, gasLimit));
                            break;
                    }
                }
            }
        }
        else {
            throw new BlockChainException(WRONG_FILE_FORMAT);
        }
    }
}
