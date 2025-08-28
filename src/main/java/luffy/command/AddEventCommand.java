package luffy.command;

import java.io.IOException;
import java.time.LocalDateTime;
import luffy.exception.LuffyException;
import luffy.task.TaskList;
import luffy.task.Event;
import luffy.ui.Ui;
import luffy.storage.Storage;
import luffy.parser.Parser;

/**
 * Command to add an event task.
 */
public class AddEventCommand extends Command {
    private String description;
    private String fromStr;
    private String toStr;

    public AddEventCommand(String description, String fromStr, String toStr) {
        this.description = description;
        this.fromStr = fromStr;
        this.toStr = toStr;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LuffyException, IOException {
        // Try to parse as date/time, fall back to string if parsing fails
        Event event;
        try {
            LocalDateTime from = Parser.parseDateTime(fromStr);
            LocalDateTime to = Parser.parseDateTime(toStr);

            // Validate that start time is before end time
            Parser.validateEventTimes(from, to, fromStr, toStr);

            event = new Event(description, from, to);
        } catch (LuffyException e) {
            // If date parsing fails, create with strings (backward compatibility)
            event = new Event(description, fromStr, toStr);
        }

        tasks.add(event);
        storage.save(tasks.getTasks());
        System.out.println(
                "HAI! TASK ADDED:\n" + event.toString() + "\n" + tasks.getTaskCountMessage());
    }
}
