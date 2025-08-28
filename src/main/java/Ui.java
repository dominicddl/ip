import java.util.Scanner;

/**
 * Handles interactions with the user.
 */
public class Ui {
    private Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Shows the welcome message.
     */
    public void showWelcome() {
        String greet = "Hello! I'm Luffy\n" + "Be my crewmate!";
        System.out.println(greet);
    }

    /**
     * Shows the goodbye message.
     */
    public void showGoodbye() {
        String goodbye = "Bye! See you next time!\n" + "I'll be waiting for you to join my crew!\n";
        System.out.println(goodbye);
    }

    /**
     * Reads a command from the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Checks if there is a next line available.
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Closes the scanner.
     */
    public void close() {
        scanner.close();
    }
}
