package luffy.task;

import java.time.LocalDateTime;
import luffy.util.DateTimeUtil;

/**
 * Represents a deadline task, which is a task that needs to be completed by a specific date and
 * time. Deadline tasks are displayed with a "[D]" prefix and include the due date/time. Supports
 * both LocalDateTime objects (preferred) and string representations (for backward compatibility).
 */
public class Deadline extends Task {
    private LocalDateTime by;
    private String byString; // For backward compatibility with old string-based data

    /**
     * Creates a new deadline task with the specified description and due date/time. This is the
     * preferred constructor for new deadline tasks.
     *
     * @param description the description of the deadline task
     * @param by the date and time when this task is due
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "Deadline date cannot be null";
        this.by = by;
        this.byString = null;
    }

    /**
     * Creates a new deadline task with the specified description and due date as a string. This
     * constructor is used for backward compatibility when loading old data.
     *
     * @param description the description of the deadline task
     * @param byString the due date/time as a string
     */
    public Deadline(String description, String byString) {
        super(description);
        assert byString != null : "Deadline date string cannot be null";
        assert !byString.trim().isEmpty() : "Deadline date string cannot be empty";
        this.by = null;
        this.byString = byString;
    }

    /**
     * Returns the due date and time of this deadline task as a LocalDateTime object.
     *
     * @return the due date and time, or null if this deadline uses string representation
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns the due date and time of this deadline task as a formatted string. For LocalDateTime
     * objects, formats using DateTimeUtil. For string-based deadlines, returns the original string
     * for backward compatibility.
     *
     * @return the formatted due date and time string
     */
    public String getByAsString() {
        if (by != null) {
            return DateTimeUtil.formatDateTime(by);
        } else {
            return byString; // Return original string for backward compatibility
        }
    }

    /**
     * Checks if this deadline task has a LocalDateTime object (as opposed to string
     * representation).
     *
     * @return true if this deadline has a LocalDateTime, false if it uses string representation
     */
    public boolean hasDateTime() {
        return by != null;
    }

    /**
     * Returns a string representation of this deadline task. The format is "[D][status] description
     * (by: due_date_time)".
     *
     * @return the string representation of this deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.getStatusIcon() + " " + super.getDescription() + " (by: "
                + getByAsString() + ")";
    }
}
