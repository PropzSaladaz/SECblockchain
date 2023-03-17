package pt.tecnico.blockchain;

import org.junit.Ignore;
import org.junit.Test;
import pt.tecnico.blockchain.Path.ModulePath;
import pt.tecnico.blockchain.Path.Path;
import pt.tecnico.blockchain.console.Console;
import pt.tecnico.blockchain.console.ConsoleLauncher;
import pt.tecnico.blockchain.console.MavenConsole;

import java.io.IOException;

public class LaunchConsoleTests
{
    private static Path rootModule = new ModulePath();

    @Ignore
    @Test
    public void launchWindowsConsole() throws IOException {
        // Should open a console, print "hi" , wait for a ping, and then close
        Console console = new ConsoleLauncher("echo hi",
                "ECHO Hi" , "ping -n 6 127.0.0.1 > nul"); // ping used to make console open till exit
        console.setDirectory(rootModule.toString());
        console.setTitle("This title");
        console.launch();
    }

    @Ignore
    @Test
    public void launchConsoleAndEcho() throws IOException {
        // Should open a console and echo "Hello"
        String memberPath = rootModule.getParent().append("blockchain-member").getPath();
        Console console = new ConsoleLauncher("echo hello");
        console.setDirectory(memberPath);
        console.launch();
    }

    @Ignore
    @Test
    public void launchMavenConsole() throws IOException {
        // Should open a console and execute a blockchain-member java program passing the following 2 arguments
        String memberPath = rootModule.getParent().append("blockchain-member").getPath();
        String configPath = rootModule.append("omit-messages.in").getPath();
        String processId = "2";
        Console console = new MavenConsole(processId, configPath, "-debug");
        console.setDirectory(memberPath);
        console.setTitle("This member");
        console.launch();
    }


}
