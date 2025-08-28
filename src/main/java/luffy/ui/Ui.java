package luffy.ui;

import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDateTime;
import luffy.task.Task;
import luffy.util.DateTimeUtil;

/**
 * Handles interactions with the user, including input/output operations. This class manages console
 * I/O for the Luffy task management system, providing methods to display messages and read user
 * commands.
 */
public class Ui {
    private Scanner scanner;

    /**
     * Creates a new Ui instance and initializes the scanner for reading user input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome message to the user. Shows Luffy's greeting when the application starts.
     */
    public void showWelcome() {
        String greet = "Hello! I'm Luffy\n" + "Be my crewmate!";
        System.out.println(greet);
    }

    /**
     * Displays the goodbye message to the user. Shows Luffy's farewell when the application exits.
     */
    public void showGoodbye() {
        String goodbye = "Bye! See you next time!\n" + "I'll be waiting for you to join my crew!\n";
        System.out.println(goodbye);
    }

    /**
     * Reads a command from the user input and trims whitespace.
     *
     * @return the user's command as a trimmed string
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Checks if there is another line of input available from the user.
     *
     * @return true if there is a next line available, false otherwise
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Displays an error message when tasks cannot be loaded from the file. Informs the user that
     * the application will start with an empty task list.
     */
    public void showLoadingError() {
        System.out.println("OOPS!!! Couldn't load tasks from file. Starting with empty task list.");
    }

    /**
     * Displays a decorative divider line to separate sections of output.
     */
    public void showLine() {
        System.out.println("    ____________________________________________________________");
    }

    /**
     * Displays a general error message to the user.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Displays all tasks that occur on a specific date. Shows either a list of matching tasks or a
     * message indicating no tasks were found.
     *
     * @param matchingTasks the list of tasks that occur on the specified date
     * @param targetDate the date being queried
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
     * Closes the scanner to free up system resources. Should be called when the application is
     * shutting down.
     */
    public void close() {
        scanner.close();
    }
}
