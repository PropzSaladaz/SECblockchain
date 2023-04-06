package pt.tecnico.blockchain;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static pt.tecnico.blockchain.Logger.TextColor.*;

public class Logger {
    private static boolean debug = false;
    private static final String defaultCode = "\033[%sm";
    private static final String hexaCode = "\u001b[%sm";

    enum TextColor {
        RESET("0"),
        BLACK("30"),
        RED("31"),
        GREEN("32"),
        YELLOW("33"),
        BLUE("34"),
        MAGENTA("35"),
        CYAN("36"),
        WHITE("37"),

        HEXA_GREY("38;5;242");

        private String colorCode;

        TextColor(String colorCode) {
            this.colorCode = colorCode;
        }

        public String getCode() {
            return colorCode;
        }
    }

    public static void setDebug() {
        debug = true;
    }

    public static void logByzantine(String message) {
        printMessageWithColor("[BYZANTINE]: " + message, MAGENTA);
    }

    public static void logBehavior(String message) {
        printMessageWithColor("[BEHAVIOR]: " + message, GREEN);
    }

    public static void logError(String message) {
        printMessageWithColor("[ERROR]: " + message, RED);
    }

    public static void logError(String message, Exception e) {
        printMessageWithColor("[ERROR]: " + message, e, RED);
    }

    public static void logInfo(String message) {
        printMessageWithColor("[INFO]: " + message, CYAN);
    }

    public static void logWarning(String message) {
        printMessageWithColor("[WARNING]: " + message, YELLOW);
    }

    public static void logWarning(String message, Exception e) {
        printMessageWithColor("[WARNING]: " + message, e, YELLOW);
    }

    public static void logDebug(String message) {
        if (debug) printMessageWithHexaColorAndTime(message, HEXA_GREY);
    }

    public static void logDebugPrimary(String message) {
        if (debug) printMessageWithColorAndTime(message, GREEN);
    }

    public static void logDebugSecondary(String message) {
        if (debug) printMessageWithColorAndTime(message, YELLOW);
    }

    private static synchronized void printMessageWithColor(String message, TextColor color) {
        System.out.println(String.format(defaultCode, color.getCode()) +
                message + String.format(defaultCode, RESET.getCode()));
    }
    private static synchronized void printMessageWithColor(String message, Exception e, TextColor color) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        System.out.println(String.format(defaultCode, color.getCode()) +
                message + "\n" + sw + String.format(defaultCode, RESET.getCode()));
    }

    private static synchronized void printMessageWithColorAndTime(String message, TextColor color) {
        System.out.println(getCurrentTime() + "\n" + String.format(defaultCode, color.getCode()) +
                message + String.format(defaultCode, RESET.getCode()));
    }

    private static synchronized void printMessageWithColorAndTime(String message, Exception e, TextColor color) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        System.out.println(getCurrentTime() + "\n" + String.format(defaultCode, color.getCode()) +
                message + "\n" + sw + String.format(defaultCode, RESET.getCode()));
    }

    private static synchronized void printMessageWithHexaColor(String message, TextColor color) {
        System.out.println(String.format(hexaCode, color.getCode()) +
                message + String.format(hexaCode, RESET.getCode()));
    }

    private static synchronized void printMessageWithHexaColorAndTime(String message, TextColor color) {
        System.out.println(getCurrentTime() + "\n" + String.format(hexaCode, color.getCode()) +
                message + String.format(hexaCode, RESET.getCode()));
    }

    private static String getCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        return currentTime.format(formatter);
    }


}
