package luffy.command;

import java.io.IOException;
import luffy.exception.LuffyException;
import luffy.task.TaskList;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Command to mark a task as done.
 */
public class MarkCommand extends Command {
    private int taskNumber;

    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LuffyException, IOException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new LuffyException("Task " + taskNumber + "? That doesn't exist! I only have "
                    + tasks.size() + " tasks!");
        }

        tasks.get(taskNumber - 1).setDone(true);
        storage.save(tasks.getTasks());
        ui.showTaskMarked(tasks.get(taskNumber - 1).toString());
    }
}
