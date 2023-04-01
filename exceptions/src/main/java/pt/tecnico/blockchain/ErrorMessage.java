package pt.tecnico.blockchain;

public enum ErrorMessage {
    INCORRECT_INITIATOR_ARGUMENTS("Incorrect arguments! Must be in the following format: \n" +
            "program <input_config_file> | program <input_config_file> -debug"),
    INVALID_PROCESS_ARGUMENTS("Incorrect arguments! Must be in the following format: \n" +
            "<ID> <configPath> | <ID> <configPath> -debug"),
    INVALID_PROCESS_TYPE("Invalid process type. Must be either M for blockchain member or C for client"),
    INVALID_BEHAVIOR_OPERATOR("Invalid behavior operator. Must be either C, O or A"),
    WRONG_FILE_FORMAT("Config file has wrong format. Must follow the following format: \n" +
            "" +
            "Commands\n" +
            "\n" +
            "   Create a process\n" +
            "   P <id> <type> <hostname>:<port>\n" +
            "       -> <id> : integer number for the process id\n" +
            "       -> <type> : Process type: 'L' - leader,\n" +
            "                                 'M' - blockchain member,\n" +
            "                                 'C' - client\n" +
            "\n" +
            "   Slot duration\n" +
            "   T <duration>\n" +
            "       -> <duration> : time of each slot in milliseconds\n" +
            "\n" +
            "   Arbitrary behavior for blochcain  members\n" +
            "   A <id> <operations>\n" +
            "       -> <operations> : <operation> <operations>*\n" +
            "       -> <operation> : (<id>, <operator>) | (<id>, A, <id>)\n" +
            "                           -> <id> : member id (cannot be the leader)\n" +
            "                           -> <operator> : 'O' - Omit messages, \n" +
            "                                           'C' - Arbitrarly corrupt messages,\n" +
            "                                           'A' - authenticate as process with <id>\n" +
            "\n" +
            "   R <id> <requests>\n" +
            "       -> <requests> : <request> <requests>*\n" +
            "       -> <request> : (<id>, \"<string>\", <delay>)\n" +
            "               -> <id> : client id\n" +
            "               -> <string> : Any combination of characters\n" +
            "               -> <delay> : time of delay for the request since entering the slot in millis"),

    COULD_NOT_INIT_PROCESS("Error initiating a blockchain process: %s"),
    INVALID_CLIENT_TRANSFER_ARGS("Could not parse config file: Client 'Transfer' request expects two integer arguments!"),
    INVALID_CLIENT_CHECK_BALANCE_ARGS("Could not parse config file: Client 'Check_balance' request expects 'W' or 'S' as argument"),
    COULD_NOT_LOAD_CONFIG_FILE("Could not load config file: %s"),
    MEMBER_DOES_NOT_EXIST("No member with id %s exist in the configuration file"),
    CLIENT_DOES_NOT_EXIST("No client with id %s exist in the configuration file"),
    UNSUPPORTED_OS("%s operating system is not supported!");


    public final String label;

    ErrorMessage(String label) { this.label = label; }
}
