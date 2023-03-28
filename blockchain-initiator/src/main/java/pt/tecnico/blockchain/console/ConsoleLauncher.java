package pt.tecnico.blockchain.console;

import pt.tecnico.blockchain.BlockChainException;
import pt.tecnico.blockchain.Path.ModulePath;
import pt.tecnico.blockchain.Path.Path;
import pt.tecnico.blockchain.console.commands.LaunchCommands;
import pt.tecnico.blockchain.console.commands.LinuxLaunch;
import pt.tecnico.blockchain.console.commands.WindowsLaunch;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static pt.tecnico.blockchain.ErrorMessage.UNSUPPORTED_OS;

public class ConsoleLauncher extends BaseConsole implements Console {

    private LaunchCommands launchCommands;


    public ConsoleLauncher() {
        setDirectory(new ModulePath().getPath());
    }

    public ConsoleLauncher(String... commands) {
        setDirectory(new ModulePath().getPath());
        setCommands(commands);
    }

    @Override
    public Process launch() throws IOException {
        setOSConsoleLaunchCommands(windowTitle, commands);
//        System.out.println(Arrays.toString(launchCommands.getCommands()));
        ProcessBuilder pb = new ProcessBuilder(launchCommands.getCommands());
        pb.directory(new File(consoleDir)); // set console path
        return pb.start();
    }

    private void setOSConsoleLaunchCommands(String windowTitle, String... commands) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            launchCommands = windowTitle.equals("") ?
                    new WindowsLaunch(commands) : // default console title
                    new WindowsLaunch(windowTitle, commands);
        }
        else if (os.contains("linux")) {
            launchCommands = windowTitle.equals("") ?
                    new LinuxLaunch(commands) : // default console title
                    new LinuxLaunch(windowTitle, commands);
        }
        else throw new BlockChainException(UNSUPPORTED_OS, os);

    }
}
