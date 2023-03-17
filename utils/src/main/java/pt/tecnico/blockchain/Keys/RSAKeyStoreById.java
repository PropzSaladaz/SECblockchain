package pt.tecnico.blockchain.Keys;



import pt.tecnico.blockchain.RSAKeyReader;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class RSAKeyStoreById {

    private static Map<Integer, PrivateKey> privateKeys;
    private static Map<Integer, PublicKey> publicKeys;

    public RSAKeyStoreById() {
        privateKeys = new HashMap<>();
        publicKeys = new HashMap<>();
    }

    public static PublicKey getPublicKey(int id) {
        return publicKeys.get(id);
    }

    public static PrivateKey getPrivateKey(int id) {
        return privateKeys.get(id);
    }

    public static void addPrivates(String directoryPath) throws Exception {
        File dir = new File(directoryPath);
        for (File file : dir.listFiles()) {
            if (file.isFile()) addPrivate(file.getPath());
        }
    }

    public static void addPublics(String directoryPath) throws Exception {
        File dir = new File(directoryPath);
        for (File file : dir.listFiles()) {
            if (file.isFile()) addPublic(file.getPath());
        }
    }

    public static void addPrivate(String keyPath) throws Exception {
        Matcher fileMatcher = KeyFilename.PRIV_FILE_PATTERN_EXT.matcher(keyPath);
        if (fileMatcher.find()) {
            int id = Integer.parseInt(fileMatcher.group(KeyFilename.PROCESS_ID_GROUP));
            privateKeys.put(id, RSAKeyReader.readPrivate(keyPath));
        }
    }

    public static void addPublic(String keyPath) throws Exception {
        Matcher fileMatcher = KeyFilename.PUB_FILE_PATTERN_EXT.matcher(keyPath);
        if (fileMatcher.find()) {
            int id = Integer.parseInt(fileMatcher.group(KeyFilename.PROCESS_ID_GROUP));
            publicKeys.put(id, RSAKeyReader.readPublic(keyPath));
        }
    }

    public static int getPrivateCount() {
        return privateKeys.size();
    }

    public static int getPublicCount() {
        return publicKeys.size();
    }
}
