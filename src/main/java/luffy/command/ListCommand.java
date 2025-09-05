package luffy.command;

import luffy.task.TaskList;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Command to list all tasks.
 */
public class ListCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
