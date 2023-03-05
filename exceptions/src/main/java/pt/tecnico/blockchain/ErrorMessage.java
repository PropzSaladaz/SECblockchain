package pt.tecnico.blockchain;

public enum ErrorMessage {
    INCORRECT_INITIATOR_ARGUMENTS("Incorrect arguments! Must be in the following format: \n" +
            "program <input_config_file> | program -debug <input_debug_file>"),

    WRONG_FILE_FORMAT("Config file has wrong format. Must follow the following format: \n" +
            "" +
            "# Commands\n" +
            "\n" +
            "#   Create a process\n" +
            "#   P <id> <type> <hostname>:<port>\n" +
            "#       -> <id> : integer number for the process id\n" +
            "#       -> <type> : Process type: 'L' - leader,\n" +
            "#                                 'M' - blockchain member,\n" +
            "#                                 'C' - client\n" +
            "\n" +
            "#   Slot duration\n" +
            "#   T <duration>\n" +
            "#       -> <duration> : time of each slot in milliseconds\n" +
            "\n" +
            "#   Arbitrary behavior for blochcain  members\n" +
            "#   A <id> <operations>\n" +
            "#       -> <operations> : <operation> <operations>*\n" +
            "#       -> <operation> : (<id>, <operator>) | (<id>, A, <id>)\n" +
            "#                           -> <id> : member id (cannot be the leader)\n" +
            "#                           -> <operator> : 'O' - Omit messages, \n" +
            "#                                           'C' - Arbitrarly corrupt messages,\n" +
            "#                                           'A' - authenticate as process with <id>\n" +
            "\n" +
            "#   R <id> <requests>\n" +
            "#       -> <requests> : <request> <requests>*\n" +
            "#       -> <request> : (<id>, \"<string>\", <delay>)\n" +
            "#               -> <id> : client id\n" +
            "#               -> <string> : Any combination of characters\n" +
            "#               -> <delay> : time of delay for the request since entering the slot in millis");

    public final String label;

    ErrorMessage(String label) { this.label = label; }
}
