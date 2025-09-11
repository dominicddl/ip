package luffy.command;

import java.io.IOException;
import luffy.exception.LuffyException;
import luffy.task.TaskList;
import luffy.task.Task;
import luffy.task.Priority;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Command to set the priority of a task. This command allows users to change the priority level of
 * any task in the task list using various priority formats (HIGH/H/1, NORMAL/N/2, LOW/L/3).
 */
public class PriorityCommand extends Command {
    private int taskNumber;
    private Priority priority;

    /**
     * Creates a new PriorityCommand with the specified task number and priority.
     *
     * @param taskNumber the 1-based index of the task to modify
     * @param priority the new priority level for the task
     */
    public PriorityCommand(int taskNumber, Priority priority) {
        assert taskNumber > 0 : "Task number must be positive: " + taskNumber;
        assert priority != null : "Priority cannot be null";
        this.taskNumber = taskNumber;
        this.priority = priority;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LuffyException, IOException {
        assert tasks != null : "TaskList cannot be null";
        assert ui != null : "UI cannot be null";
        assert storage != null : "Storage cannot be null";

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new LuffyException("Task " + taskNumber + "? That doesn't exist! I only have "
                    + tasks.size() + " tasks!");
        }

        Task task = tasks.get(taskNumber - 1);
        Priority oldPriority = task.getPriority();
        task.setPriority(priority);

        storage.save(tasks.getTasks());
        ui.showPriorityChanged(task.toString(), oldPriority, priority);
    }
}
