package luffy.command;

import java.time.LocalDateTime;
import java.util.ArrayList;
import luffy.exception.LuffyException;
import luffy.task.TaskList;
import luffy.task.Task;
import luffy.ui.Ui;
import luffy.storage.Storage;
import luffy.parser.Parser;

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
