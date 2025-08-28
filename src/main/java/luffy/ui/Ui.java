package luffy.ui;

import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDateTime;
import luffy.task.Task;
import luffy.util.DateTimeUtil;

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
     * Shows loading error message.
     */
    public void showLoadingError() {
        System.out.println("OOPS!!! Couldn't load tasks from file. Starting with empty task list.");
    }

    /**
     * Shows a divider line.
     */
    public void showLine() {
        System.out.println("    ____________________________________________________________");
    }

    /**
     * Shows an error message.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Shows tasks occurring on a specific date.
     */
    public void showTasksOnDate(ArrayList<Task> matchingTasks, LocalDateTime targetDate) {
        String formattedDate = DateTimeUtil.formatDateTime(targetDate);
        if (matchingTasks.isEmpty()) {
            System.out.println("No deadlines or events found on " + formattedDate + "!");
        } else {
            System.out.println("Here are your tasks on " + formattedDate + ":");
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println((i + 1) + ". " + matchingTasks.get(i).toString());
            }
        }
    }

    /**
     * Closes the scanner.
     */
    public void close() {
        scanner.close();
    }
}
