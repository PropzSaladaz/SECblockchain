package pt.tecnico.blockchain.console;

import java.io.IOException;

public interface Console {
    String DEFAULT_TITLE = "Console";

    Process launch() throws IOException;

    void setTitle(String title);

    void setCommands(String... commands);

    void setDirectory(String directory);

}
