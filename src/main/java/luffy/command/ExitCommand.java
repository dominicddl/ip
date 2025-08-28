package luffy.command;

import luffy.task.TaskList;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Command to exit the application. This command displays a goodbye message and signals the
 * application to terminate.
 */
public class ExitCommand extends Command {

    /**
     * Executes the exit command by displaying the goodbye message. The application loop will
     * terminate after this command executes because isExit() returns true.
     *
     * @param tasks the task list (not used by this command)
     * @param ui the user interface for displaying the goodbye message
     * @param storage the storage handler (not used by this command)
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Returns true to indicate this command causes the application to exit.
     *
     * @return true, indicating this is an exit command
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
