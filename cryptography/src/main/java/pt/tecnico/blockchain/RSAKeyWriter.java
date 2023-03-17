package pt.tecnico.blockchain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;

public class RSAKeyWriter {
    public static String PRIVATE_EXT = "-priv.der";
    public static String PUBLIC_EXT = "-pub.der";

    public static void writeToFile(KeyPair keys, String filePath) throws IOException {
        String privatePath = filePath + PRIVATE_EXT;
        String publicPath = filePath + PUBLIC_EXT;
        Files.write(Paths.get(privatePath), keys.getPrivate().getEncoded());
        Files.write(Paths.get(publicPath), keys.getPublic().getEncoded());
    }
}
