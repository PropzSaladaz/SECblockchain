package pt.tecnico.blockchain;

import pt.tecnico.blockchain.Keys.KeyFilename;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static pt.tecnico.blockchain.Path.BlockchainPaths.MEMBER_KEYDIR_PATH;

public class TempFile {
    private boolean created = false;
    private String path;
    private String content;

    public TempFile(String path, String content) {
        this.path = path;
        this.content = content;
    }

    public void create() {
        File file = new File(path);

        try (FileWriter writer = new FileWriter(file, false)) {
            if (file.createNewFile()) { // If there was no file with the same name
                writer.write(content);
                created = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteIfDidntExist() {
        if (created) {
            File file = new File(path);
            file.delete();
            created = false;
        }
    }
}
