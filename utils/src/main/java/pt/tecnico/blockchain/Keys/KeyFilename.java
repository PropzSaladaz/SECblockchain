package pt.tecnico.blockchain.Keys;

import pt.tecnico.blockchain.Path.ModulePath;
import pt.tecnico.blockchain.RSAKeyWriter;

import java.util.regex.Pattern;

/**
 * Used to return the filename structure / patterns for keyfiles in the context of
 * the blockchain application
 */
public class KeyFilename {
    private static final String FILE_REGEX_BASE = "(?<pType>[\\w.]+)-(?<id>[\\d+])";

    public static final String PROCESS_TYPE_GROUP = "pType";
    public static final String PROCESS_ID_GROUP = "id";
    public static final Pattern PRIV_FILE_PATTERN_EXT = Pattern.compile(FILE_REGEX_BASE + RSAKeyWriter.PRIVATE_EXT);
    public static final Pattern PUB_FILE_PATTERN_EXT = Pattern.compile(FILE_REGEX_BASE + RSAKeyWriter.PUBLIC_EXT);

    public static String get(String processType, int id) {
        return processType + "-" + id;
    }

    public static String getWithPrivExtension(String processType, int id) {
        return get(processType, id) + RSAKeyWriter.PRIVATE_EXT;
    }

    public static String getWithPubExtension(String processType, int id) {
        return get(processType, id) + RSAKeyWriter.PUBLIC_EXT;
    }
}
