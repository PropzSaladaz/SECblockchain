package pt.tecnico.blockchain.console.commands;

import pt.tecnico.blockchain.console.Console;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class LaunchCommands {
    String windowTitle = Console.DEFAULT_TITLE;
    String[] commands;

    public String[] getCommands() {
        return commands;
    }

    protected void setCommands(String[] commands) {
        StringBuilder sb = new StringBuilder();
        sb.append(getCommandDelimiter());
         if (getCommandInNewConsole()) sb.append(getCommandSeparator());
        for (String command : commands) {
            sb.append(parseCommand(command));
            sb.append(getCommandSeparator());
        }
        sb.append(parseCommand(getEndCommand()));
        sb.append(getCommandDelimiter());
        String processedCommands = sb.toString();
        String[] commandsSplit = splitBySeparator(processedCommands);

        this.commands = Stream.concat(Arrays.stream(getBaseCommand()),
                Arrays.stream(commandsSplit)).toArray(String[]::new);
    }

    protected void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    /**
     * Override if commands are to be set between delimiters like "
     */
    protected String getCommandDelimiter() {
        return "";
    }

    /**
     * Override if no need to split commands by separator into an array
     */
    protected String[] splitBySeparator(String commands) {
        return commands.split(getCommandSeparator(), -1); // -1 used to include the separators when split
    }

    public abstract String[] getBaseCommand();

    public abstract String getWindowTitleCommand();

    public abstract String getCommandSeparator();

    /**
     * Useful to parse symbols like " which may require an additional back-slash - \",
     * or to add command separators like ";" or "&&"
     */
    public abstract String parseCommand(String command);

    public abstract String getEndCommand();

    public boolean getCommandInNewConsole() { return false; }


}
