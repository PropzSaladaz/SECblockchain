package pt.tecnico.blockchain.console;

import java.io.IOException;

public class MavenConsole implements Console {
    private String command = "mvn exec:java -Dexec.args=\"";
    private String consoleDir;

    public MavenConsole(String consoleDir, String... args) {
        this.consoleDir = consoleDir;
        for (String arg : args) {
            command += String.format(" '%s'", arg);
        }
        command += "\"";
    }

    @Override
    public Process launch() throws IOException {
        Console console = new ConsoleLauncher(consoleDir, command);
        return console.launch();
    }
}
