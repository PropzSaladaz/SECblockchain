package pt.tecnico.blockchain.Keys;



import pt.tecnico.blockchain.RSAKeyReader;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class RSAKeyStoreById {

    private Map<Integer, PrivateKey> privateKeys;
    private Map<Integer, PublicKey> publicKeys;

    public RSAKeyStoreById() {
        privateKeys = new HashMap<>();
        publicKeys = new HashMap<>();
    }

    public PublicKey getPublicKey(int id) {
        return publicKeys.get(id);
    }

    public PrivateKey getPrivateKey(int id) {
        return privateKeys.get(id);
    }

    public void addPrivates(String directoryPath) throws Exception {
        File dir = new File(directoryPath);
        for (File file : dir.listFiles()) {
            if (file.isFile()) addPrivate(file.getName());
        }
    }

    public void addPublics(String directoryPath) throws Exception {
        File dir = new File(directoryPath);
        for (File file : dir.listFiles()) {
            if (file.isFile()) addPublic(file.getName());
        }
    }

    public void addPrivate(String keyPath) throws Exception {
        Matcher fileMatcher = KeyFilename.PRIV_FILE_PATTERN_EXT.matcher(keyPath);
        if (fileMatcher.find()) {
            int id = Integer.parseInt(fileMatcher.group(KeyFilename.PROCESS_ID_GROUP));
            privateKeys.put(id, RSAKeyReader.readPrivate(keyPath));
        }
    }

    public void addPublic(String keyPath) throws Exception {
        Matcher fileMatcher = KeyFilename.PUB_FILE_PATTERN_EXT.matcher(keyPath);
        if (fileMatcher.find()) {
            int id = Integer.parseInt(fileMatcher.group(KeyFilename.PROCESS_ID_GROUP));
            publicKeys.put(id, RSAKeyReader.readPublic(keyPath));
        }
    }
}
