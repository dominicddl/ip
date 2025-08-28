import java.io.IOException;

/**
 * Represents a command that can be executed.
 */
public abstract class Command {

    /**
     * Executes the command.
     * 
     * @param tasks the task list
     * @param ui the user interface
     * @param storage the storage handler
     * @throws LuffyException if there is an error during execution
     * @throws IOException if there is an error with file operations
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws LuffyException, IOException;

    /**
     * Returns whether this command causes the application to exit.
     * 
     * @return true if this is an exit command, false otherwise
     */
    public boolean isExit() {
        return false;
    }
}
