package pt.tecnico.blockchain.console;

import java.io.IOException;

/**
 * Represents a "mvn exe:java -Dexec.args='arg1 arg2 ...'" on a terminal console with the specified
 * input arguments and in the specified directory given in the constructor
 */
public class MavenConsole extends BaseConsole implements Console {
    private final String baseCommand = "mvn exec:java -Dexec.args=\"";
    private String command = baseCommand;

    public MavenConsole(String... args) {
        for (String arg : args) {
            command += String.format(" '%s'", arg);
        }
        command += "\"";
    }

    @Override
    public Process launch() throws IOException {
        Console console = new ConsoleLauncher(command);
        console.setDirectory(consoleDir);
        console.setTitle(windowTitle);
        return console.launch();
    }

    @Override
    public void setCommands(String... commands) {
        command = baseCommand;
        for (String arg : commands) {
            command += String.format(" '%s'", arg);
        }
        command += "\"";
    }

}
