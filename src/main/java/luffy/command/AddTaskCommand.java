package luffy.command;

import java.io.IOException;
import luffy.exception.LuffyException;
import luffy.task.TaskList;
import luffy.task.Task;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Abstract base class for commands that add tasks to the task list. This class provides common
 * functionality for adding tasks and handling the persistence and UI updates. Subclasses need to
 * implement the createTask method to specify how to create their specific task type.
 */
public abstract class AddTaskCommand extends Command {

    /**
     * Creates the specific task based on the command's parameters.
     *
     * @return the task to be added to the task list
     * @throws LuffyException if there's an error creating the task
     */
    protected abstract Task createTask() throws LuffyException;

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LuffyException, IOException {
        Task task = createTask();
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task.toString(), tasks.getTaskCountMessage());
    }
}
