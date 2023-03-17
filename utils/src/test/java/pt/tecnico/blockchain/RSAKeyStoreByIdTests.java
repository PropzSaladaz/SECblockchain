package pt.tecnico.blockchain;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import pt.tecnico.blockchain.Keys.KeyFilename;
import pt.tecnico.blockchain.Keys.RSAKeyStoreById;
import pt.tecnico.blockchain.Path.ModulePath;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

@Ignore
public class RSAKeyStoreByIdTests {
    private static ModulePath tmpKeys = new ModulePath()
            .append("src")
            .append("test")
            .append("java")
            .append("pt")
            .append("tecnico")
            .append("blockchain")
            .append("tmpkeys");

    private static TempFile Cpub1;
    private static TempFile Cpub2;
    private static TempFile Mpub1;
    private static TempFile Mpub2;



    @BeforeClass
    public static void createTempKeys() throws NoSuchAlgorithmException, IOException {
        String fullPath;

        fullPath = getPubKeyFullPath("C", 1);
        RSAKeyWriter.writeToFile(KeyGenerate.generateRSAkeys(), fullPath);
        Cpub1 = new TempFile(fullPath, "");

        fullPath = getPubKeyFullPath("C", 2);
        RSAKeyWriter.writeToFile(KeyGenerate.generateRSAkeys(), fullPath);
        Cpub2 = new TempFile(fullPath, "");

        fullPath = getPubKeyFullPath("M", 3);
        RSAKeyWriter.writeToFile(KeyGenerate.generateRSAkeys(), fullPath);
        Mpub1 = new TempFile(fullPath, "");

        fullPath = getPubKeyFullPath("M", 4);
        RSAKeyWriter.writeToFile(KeyGenerate.generateRSAkeys(), fullPath);
        Mpub2 = new TempFile(fullPath, "");
    }

    @Test
    public void readPublics() throws Exception {
        RSAKeyStoreById.addPublics(tmpKeys.getPath());

        assertEquals(5, RSAKeyStoreById.getPublicCount());
    }

    @AfterClass
    public static void deleteTempKeys() {
        Cpub1.deleteIfDidntExist();
        Cpub2.deleteIfDidntExist();
        Mpub1.deleteIfDidntExist();
        Mpub2.deleteIfDidntExist();
    }

    public static String getPubKeyFullPath(String type, int id) {
        if (type.equals("C")) {
            String keyFilename = KeyFilename.getWithPubExtension("Client", id);
            return tmpKeys.append(keyFilename).getPath();
        }
        else {
            String keyFilename = KeyFilename.getWithPubExtension("Member", id);
            return tmpKeys.append(keyFilename).getPath();
        }
    }
}
