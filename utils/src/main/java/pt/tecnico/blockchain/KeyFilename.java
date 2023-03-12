package pt.tecnico.blockchain;

import java.util.regex.Pattern;

/**
 * Used to return the filename structure / patterns for keyfiles in the context of
 * the blockchain application
 */
public class KeyFilename {
    public final String PROCESS_TYPE = "pType";
    private final String FILE_REGEX
    public final Pattern KEY_FILE_PATTERN = Pattern.compile("(?<pType>[\\w.]+)-(?<id>[\\d+])");
    public final Pattern PRIV_FILE_PATTERN_EXT = Pattern.compile("(?<pType>[\\w.]+)-(?<id>[\\d+])");
    public final Pattern PUB_FILE_PATTERN_EXT = Pattern.compile("(?<pType>[\\w.]+)-(?<id>[\\d+])");


    public static String get(String processType, int id) {
        return processType + "-" + id;
    }
}
