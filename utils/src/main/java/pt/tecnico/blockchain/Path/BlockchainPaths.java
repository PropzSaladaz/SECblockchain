package pt.tecnico.blockchain.Path;

public class BlockchainPaths {
    public static final String MEMBER_MODULE_NAME = "blockchain-member";
    public static final String CLIENT_MODULE_NAME = "client";
    public static final String INITIATOR_MODULE_NAME = "blockchain-initiator";

    public static final String[] KEYS_DIRECTORY_PATH = {"src", "main", "resources"};

    public static final ModulePath MEMBER_KEYDIR_PATH = getKeysPath(MEMBER_MODULE_NAME);
    public static final ModulePath INITIATOR_KEYDIR_PATH = getKeysPath(INITIATOR_MODULE_NAME);
    public static final ModulePath CLIENT_KEYDIR_PATH = getKeysPath(CLIENT_MODULE_NAME);


    private static ModulePath getKeysPath(String moduleName) {
        ModulePath path = new ModulePath()
                .getParent()
                .append(moduleName);
        for (String subDir : KEYS_DIRECTORY_PATH) {
            path = path.append(subDir);
        }
        return path;
    }
}
