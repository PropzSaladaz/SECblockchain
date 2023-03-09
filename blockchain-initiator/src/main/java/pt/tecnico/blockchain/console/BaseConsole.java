package pt.tecnico.blockchain.console;

public abstract class BaseConsole implements Console {
    String consoleDir;
    String[] commands;
    String windowTitle = "";

    public void setTitle(String title) {
        windowTitle = title;
    }

    public void setCommands(String... commands) {
        this.commands = commands;
    }

    public void setDirectory(String directory) {
        consoleDir = directory;
    }
}
