package luffy.command;

import java.time.LocalDateTime;
import luffy.exception.LuffyException;
import luffy.task.Task;
import luffy.task.Deadline;
import luffy.parser.Parser;

/**
 * Command to add a deadline task.
 */
public class AddDeadlineCommand extends AddTaskCommand {
    private String description;
    private String byStr;

    public AddDeadlineCommand(String description, String byStr) {
        this.description = description;
        this.byStr = byStr;
    }

    @Override
    protected Task createTask() throws LuffyException {
        // Try to parse as date/time, fall back to string if parsing fails
        try {
            LocalDateTime by = Parser.parseDateTime(byStr);
            return new Deadline(description, by);
        } catch (LuffyException e) {
            // If date parsing fails, create with string (backward compatibility)
            return new Deadline(description, byStr);
        }
    }
}
