package luffy.command;

import luffy.task.TaskList;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Command to exit the application.
 */
public class ExitCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
