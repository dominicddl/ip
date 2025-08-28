import java.io.IOException;

/**
 * Command to delete a task.
 */
public class DeleteCommand extends Command {
    private int taskNumber;

    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

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
