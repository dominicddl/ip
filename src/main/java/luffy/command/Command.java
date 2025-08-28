package luffy.command;

import java.io.IOException;
import luffy.exception.LuffyException;
import luffy.task.TaskList;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Represents a command that can be executed in the Luffy task management system. This is the base
 * class for all command types, implementing the Command pattern. Each concrete command encapsulates
 * a specific user action and its execution logic.
 */
public abstract class Command {

    /**
     * Executes the command with the provided dependencies. This method contains the main logic for
     * the command and is implemented by each concrete command class.
     * 
     * @param tasks the task list to operate on
     * @param ui the user interface for displaying messages
     * @param storage the storage handler for persisting changes
     * @throws LuffyException if there is an error during command execution
     * @throws IOException if there is an error with file operations
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws LuffyException, IOException;

    /**
     * Returns whether this command causes the application to exit. Most commands return false; only
     * ExitCommand returns true.
     * 
     * @return true if this is an exit command, false otherwise
     */
    public boolean isExit() {
        return false;
    }
}
