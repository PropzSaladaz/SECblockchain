package pt.tecnico.blockchain;



import pt.tecnico.blockchain.Path.ModulePath;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RSAKeyStoreById {
    private final Pattern FILE_PATTERN;
    private final String ID = "id";

    private Map<Integer, PrivateKey> privateKeys;
    private Map<Integer, PublicKey> publicKeys;

    public RSAKeyStoreById(Pattern fileRegex) {
        privateKeys = new HashMap<>();
        publicKeys = new HashMap<>();
        this.FILE_PATTERN = fileRegex;
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
            String filename = file.getName();
            if (file.isFile() && filename.endsWith(RSAKeyWriter.PRIVATE_EXT)) {
                addPrivate(filename);
            }
        }
    }

    public void addPublics(String directoryPath) throws Exception {
        File dir = new File(directoryPath);
        for (File file : dir.listFiles()) {
            String filename = file.getName();
            if (file.isFile() && filename.endsWith(RSAKeyWriter.PUBLIC_EXT)) {
                addPublic(filename);
            }
        }
    }

    public void addPrivate(String keyPath) throws Exception {
        Matcher fileMatcher = FILE_PATTERN.matcher(keyPath);
        if (fileMatcher.find()) {
            int id = Integer.parseInt(fileMatcher.group(ID));
            privateKeys.put(id, RSAKeyReader.readPrivate(keyPath));
        }
    }

    public void addPublic(String keyPath) throws Exception {
        Matcher fileMatcher = FILE_PATTERN.matcher(keyPath);
        if (fileMatcher.find()) {
            int id = Integer.parseInt(fileMatcher.group(ID));
            publicKeys.put(id, RSAKeyReader.readPublic(keyPath));
        }
    }
}
