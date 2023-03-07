package pt.tecnico.blockchain.console;

import java.io.IOException;

public interface Console {

    String commandSeparator = System.getProperty("os.name").startsWith("Windows") ? " && " : " ; ";
    Process launch() throws IOException;

}
