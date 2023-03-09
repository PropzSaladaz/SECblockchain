package pt.tecnico.blockchain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;

public class RSAKeyWriter {
    public static void writeToFile(KeyPair keys, String filePath) throws IOException {
        String privatePath = filePath + "-priv.der";
        String publicPath = filePath + "-pub.der";
        Files.write(Paths.get(privatePath), keys.getPrivate().getEncoded());
        Files.write(Paths.get(publicPath), keys.getPublic().getEncoded());
    }
}
