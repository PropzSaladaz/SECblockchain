package pt.tecnico.blockchain.console.commands;

import java.util.Arrays;
import java.util.stream.Stream;

public class LinuxLaunch extends LaunchCommands {

    public LinuxLaunch(String... commands) {
        setCommands(commands);
    }

    public LinuxLaunch(String windowTitle, String... commands) {
        setWindowTitle(windowTitle);
        setCommands(commands);
    }

    @Override
    public String[] getBaseCommand() {
        String[] base = "gnome-terminal --title ".split(" ");
        String[] title = {windowTitle};
        String[] base2 = " --disable-factory -- bash -c ".split(" ");
        return Stream.concat(Stream.concat(Arrays.stream(base), Arrays.stream(title)),
                Arrays.stream(base2)).toArray(String[]::new);
    }

    @Override
    public String getWindowTitleCommand() {
        return "";
    }

    @Override
    public String getEndCommand() {
        return "read -p 'Press ENTER to exit' ; exit";
    }

    @Override
    public String getCommandSeparator() {
        return ";";
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


}
