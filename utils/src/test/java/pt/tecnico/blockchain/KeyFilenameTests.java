package pt.tecnico.blockchain;

import org.junit.Test;
import pt.tecnico.blockchain.Keys.KeyFilename;
import pt.tecnico.blockchain.Path.BlockchainPaths;

import java.io.IOException;
import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pt.tecnico.blockchain.Keys.KeyFilename.PRIV_FILE_PATTERN_EXT;
import static pt.tecnico.blockchain.Path.BlockchainPaths.MEMBER_KEYDIR_PATH;

public class KeyFilenameTests {

    @Test
    public void matchMemberPrivateKey() throws IOException {
        // given
        boolean matched;
        int id = -1, actualID = 5;
        String keyFilename = KeyFilename.getWithPrivExtension("Member", actualID);
        String fullKeyPath = MEMBER_KEYDIR_PATH.append(keyFilename).getPath();
        TempFile file = new TempFile(fullKeyPath, "");
        file.create();

        // when
        Matcher fileMatcher = PRIV_FILE_PATTERN_EXT.matcher(fullKeyPath);
        matched = fileMatcher.find();
        if (matched) id = Integer.parseInt(fileMatcher.group(KeyFilename.PROCESS_ID_GROUP));

        // then
        assertTrue(matched);
        assertEquals(actualID, id);
        file.deleteIfDidntExist();
    }
}
