package pt.tecnico.blockchain.console.commands;

import java.util.Arrays;
import java.util.stream.Stream;

public class WindowsLaunch extends LaunchCommands {

    public WindowsLaunch(String... commands) {
        setCommands(commands);
    }

    public WindowsLaunch(String windowTitle, String... commands) {
        setWindowTitle(windowTitle);
        setCommands(commands);
    }

    @Override
    public String[] getBaseCommand() {
        String[] base = "cmd /c start /wait cmd /c ".split(" ");
        String[] title = { "title " + windowTitle };
        return (Stream.concat(Arrays.stream(base), Arrays.stream(title))).toArray(String[]::new);
    }

    @Override
    public String getWindowTitleCommand() {
        return "";
    }

    @Override
    public String getEndCommand() {
        return " pause & exit";
    }

    @Override
    public String getCommandSeparator() {
        return "&";
    }

    @Override
    public String parseCommand(String command) {
        return command;
    }

    @Override
    protected String[] splitBySeparator(String commands) {
        // no need to split the commands since they are all under "" in the same command
        return new String[]{commands};
    }

    @Override
    public boolean getCommandInNewConsole() {
        return true;
    }


}
