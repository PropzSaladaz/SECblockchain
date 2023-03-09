package pt.tecnico.blockchain.console.commands;

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
        return "cmd /c start cmd.exe /k".split(" ");
    }

    @Override
    public String getWindowTitleCommand() {
        return null;
    }

    @Override
    public String getEndCommand() {
        return ""; // No need, the /k flag on the base command already sets the window to be persistent
    }

    @Override
    public String getCommandSeparator() {
        return "&&";
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
