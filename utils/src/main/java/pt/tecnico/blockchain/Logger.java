package pt.tecnico.blockchain;

import static pt.tecnico.blockchain.Logger.TextColor.*;

public class Logger {
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

    public static void logByzantine(String message) {
        printMessageWithColor(message, MAGENTA);
    }

    public static void logWarning(String message) {
        printMessageWithColor(message, YELLOW);
    }

    public static void logDebug(String message) {
        printMessageWithHexaColor(message, HEXA_GREY);
    }

    private static synchronized void printMessageWithColor(String message, TextColor color) {
        System.out.println(String.format(defaultCode, color.getCode()) +
                message + String.format(defaultCode, RESET.getCode()));
    }

    private static synchronized void printMessageWithHexaColor(String message, TextColor color) {
        System.out.println(String.format(hexaCode, color.getCode()) +
                message + String.format(hexaCode, RESET.getCode()));
    }


}
