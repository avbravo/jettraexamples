package io.jettra.examples.store.util;

/**
 * Utilidades para embellecer la salida en consola mediante colores ANSI.
 */
public final class ConsoleColor {
    private ConsoleColor() {}

    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BOLD = "\u001B[1m";

    public static void printHeader(String title) {
        System.out.println();
        System.out.println(CYAN + BOLD + "═".repeat(78) + RESET);
        System.out.println(CYAN + BOLD + "  " + title + RESET);
        System.out.println(CYAN + BOLD + "═".repeat(78) + RESET);
    }

    public static void printSubHeader(String subtitle) {
        System.out.println(YELLOW + BOLD + "--- " + subtitle + " ---" + RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + "✔ " + message + RESET);
    }

    public static void printInfo(String label, Object value) {
        System.out.printf("  %s• %s:%s %s%n", BLUE, label, RESET, value);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "⚠ " + message + RESET);
    }
}
