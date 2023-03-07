package pt.tecnico.blockchain.console;

import pt.tecnico.blockchain.BlockChainException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static pt.tecnico.blockchain.ErrorMessage.UNSUPPORTED_OS;

public class ConsoleLauncher implements Console {
    private String[] commands;
    private String consoleDir;

    public ConsoleLauncher(String consoleDir, String... args) {
        String os = System.getProperty("os.name").toLowerCase();
        this.consoleDir = consoleDir;
        String[] launchCommands;

        if (os.contains("windows")) {
            launchCommands = "cmd /c start cmd.exe /k".split(" ");
        } else if (os.contains("linux")) {
            launchCommands = "/bin/bash -c".split(" ");
        }
        else {
            throw new BlockChainException(UNSUPPORTED_OS, os);
        }
        commands = Stream.concat(Arrays.stream(launchCommands), Arrays.stream(args)).toArray(String[]::new);
    }

    @Override
    public Process launch() throws IOException {
        System.out.println(Arrays.toString(commands));
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(consoleDir)); // set console path
        return pb.start();
    }
}
