package pt.tecnico.blockchain;

import org.junit.Ignore;
import org.junit.Test;
import pt.tecnico.blockchain.console.Console;
import pt.tecnico.blockchain.console.ConsoleLauncher;
import pt.tecnico.blockchain.console.MavenConsole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class LaunchConsoleTests
{
    private static Path rootModule = Paths.get(new File("").getAbsolutePath());

    @Ignore
    @Test
    public void launchConsoleLauncher() throws IOException, InterruptedException {
        // Should open a console, print "hi" , wait for a ping, and then close
        Console console = new ConsoleLauncher(rootModule.toString(), "dir" + Console.commandSeparator +
                "ECHO Hi" + Console.commandSeparator +  "ping -n 6 127.0.0.1 > nul" + Console.commandSeparator
                + "exit"
        ); // ping used to make console open till exit
        Process p = console.launch();
        p.destroy();
    }

    @Ignore
    @Test
    public void launchMavenConsole() throws IOException {
        // Should open a console and execute a blockchain-member java program passing the following 2 arguments
        String memberPath = rootModule.getParent() + File.separator + "blockchain-member";
        String configPath = memberPath + File.separator + "config.in";
        String processId = "5";
        Console console = new MavenConsole(memberPath, processId, configPath);
        console.launch();
    }
}
