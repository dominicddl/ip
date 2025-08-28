package luffy.command;

import java.io.IOException;
import luffy.exception.LuffyException;
import luffy.task.TaskList;
import luffy.task.Task;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Command to delete a task from the task list. The task is identified by its 1-based position
 * number in the list.
 */
public class DeleteCommand extends Command {
    private int taskNumber;

    /**
     * Creates a new DeleteCommand for the task at the specified position.
     *
     * @param taskNumber the 1-based position number of the task to delete
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Executes the command by removing the specified task from the task list, saving the updated
     * list to storage, and displaying a confirmation message. Validates that the task number is
     * within valid bounds.
     *
     * @param tasks the task list to remove the task from
     * @param ui the user interface for displaying messages
     * @param storage the storage handler for saving changes
     * @throws LuffyException if the task number is invalid (out of bounds)
     * @throws IOException if there is an error saving to storage
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LuffyException, IOException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new LuffyException("Task " + taskNumber + "? That doesn't exist! I only have "
                    + tasks.size() + " tasks!");
        }

        Task deletedTask = tasks.get(taskNumber - 1);
        tasks.remove(taskNumber - 1);
        storage.save(tasks.getTasks());
        System.out.println("HAI! TASK DELETED:\n" + deletedTask.toString() + "\n"
                + tasks.getTaskCountMessage());
    }
}
