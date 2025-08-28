package luffy;

import java.io.File;
import java.io.IOException;
import luffy.task.TaskList;
import luffy.storage.Storage;
import luffy.ui.Ui;
import luffy.parser.Parser;
import luffy.command.Command;
import luffy.exception.LuffyException;

/**
 * Main class for the Luffy task management application. This class orchestrates the interaction
 * between the UI, storage, and task management components. It implements the main application loop
 * using the Command pattern to process user input.
 */
public class Luffy {
    private TaskList tasks;
    private Storage storage;
    private Ui ui;

    /**
     * Creates a new Luffy application instance with the specified file path for data storage.
     * Initializes the UI, storage, and attempts to load existing tasks from the file. If loading
     * fails, starts with an empty task list and shows an error message.
     *
     * @param filePath the path to the file where tasks will be stored
     */
    public Luffy(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main application loop. Displays the welcome message and continuously processes user
     * commands until the user chooses to exit. Uses the Command pattern to parse and execute
     * commands, with proper error handling for both application and I/O exceptions.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine(); // show the divider line ("_______")
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (LuffyException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError(
                        "OOPS!!! Something went wrong with file operations: " + e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Main entry point for the Luffy application. Creates a new Luffy instance with the default
     * data file path and starts the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        new Luffy("data" + File.separator + "Luffy.txt").run();
    }
}
