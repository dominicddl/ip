import java.io.IOException;

/**
 * Command to unmark a task (mark as not done).
 */
public class UnmarkCommand extends Command {
    private int taskNumber;

    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LuffyException, IOException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new LuffyException("Task " + taskNumber + "? That doesn't exist! I only have "
                    + tasks.size() + " tasks!");
        }

        tasks.get(taskNumber - 1).setDone(false);
        storage.save(tasks.getTasks());
        System.out.println("NANI?" + "\n" + tasks.get(taskNumber - 1).getStatusIcon() + " "
                + tasks.get(taskNumber - 1).getDescription());
    }
}
