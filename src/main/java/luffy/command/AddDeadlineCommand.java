package luffy.command;

import java.io.IOException;
import java.time.LocalDateTime;
import luffy.exception.LuffyException;
import luffy.task.TaskList;
import luffy.task.Deadline;
import luffy.ui.Ui;
import luffy.storage.Storage;
import luffy.parser.Parser;

/**
 * Command to add a deadline task.
 */
public class AddDeadlineCommand extends Command {
    private String description;
    private String byStr;

    public AddDeadlineCommand(String description, String byStr) {
        this.description = description;
        this.byStr = byStr;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LuffyException, IOException {
        // Try to parse as date/time, fall back to string if parsing fails
        Deadline deadline;
        try {
            LocalDateTime by = Parser.parseDateTime(byStr);
            deadline = new Deadline(description, by);
        } catch (LuffyException e) {
            // If date parsing fails, create with string (backward compatibility)
            deadline = new Deadline(description, byStr);
        }

        tasks.add(deadline);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(deadline.toString(), tasks.getTaskCountMessage());
    }
}
