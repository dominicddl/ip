import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Command to show tasks due on a specific date.
 */
public class DueCommand extends Command {
    private String dateStr;

    public DueCommand(String dateStr) {
        this.dateStr = dateStr;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LuffyException {
        LocalDateTime targetDate;
        try {
            targetDate = Parser.parseDateTime(dateStr);
        } catch (LuffyException e) {
            throw new LuffyException("Invalid date format for 'due' command. " + e.getMessage());
        }

        ArrayList<Task> matchingTasks = tasks.getTasksOnDate(targetDate);
        ui.showTasksOnDate(matchingTasks, targetDate);
    }
}
