package luffy.command;

import java.time.LocalDateTime;
import luffy.exception.LuffyException;
import luffy.task.Task;
import luffy.task.Event;
import luffy.parser.Parser;

/**
 * Command to add an event task.
 */
public class AddEventCommand extends AddTaskCommand {
    private String description;
    private String fromStr;
    private String toStr;

    public AddEventCommand(String description, String fromStr, String toStr) {
        this.description = description;
        this.fromStr = fromStr;
        this.toStr = toStr;
    }

    @Override
    protected Task createTask() throws LuffyException {
        // Try to parse as date/time, fall back to string if parsing fails
        try {
            LocalDateTime from = Parser.parseDateTime(fromStr);
            LocalDateTime to = Parser.parseDateTime(toStr);

            // Validate that start time is before end time
            Parser.validateEventTimes(from, to, fromStr, toStr);

            return new Event(description, from, to);
        } catch (LuffyException e) {
            // If date parsing fails, create with strings (backward compatibility)
            return new Event(description, fromStr, toStr);
        }
    }
}
