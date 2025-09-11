package luffy.task;

import java.time.LocalDateTime;
import luffy.util.DateTimeUtil;

/**
 * Represents an event task, which is a task that occurs during a specific time period. Event tasks
 * are displayed with an "[E]" prefix and include both start and end times. Supports both
 * LocalDateTime objects (preferred) and string representations (for backward compatibility).
 */
public class Event extends Task {
    private LocalDateTime from;
    private LocalDateTime to;
    private String fromString; // For backward compatibility with old string-based data
    private String toString; // For backward compatibility with old string-based data

    /**
     * Creates a new event task with the specified description and time period. This is the
     * preferred constructor for new event tasks.
     *
     * @param description the description of the event task
     * @param from the start date and time of the event
     * @param to the end date and time of the event
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
        this.fromString = null;
        this.toString = null;
    }

    /**
     * Creates a new event task with the specified description and time period as strings. This
     * constructor is used for backward compatibility when loading old data.
     *
     * @param description the description of the event task
     * @param fromString the start time as a string
     * @param toString the end time as a string
     */
    public Event(String description, String fromString, String toString) {
        super(description);
        this.from = null;
        this.to = null;
        this.fromString = fromString;
        this.toString = toString;
    }

    /**
     * Returns the start date and time of this event as a LocalDateTime object.
     *
     * @return the start date and time, or null if this event uses string representation
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end date and time of this event as a LocalDateTime object.
     *
     * @return the end date and time, or null if this event uses string representation
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns the start time of this event as a formatted string. For LocalDateTime objects,
     * formats using DateTimeUtil. For string-based events, returns the original string for backward
     * compatibility.
     *
     * @return the formatted start time string
     */
    public String getFromAsString() {
        if (from != null) {
            return DateTimeUtil.formatDateTime(from);
        } else {
            return fromString; // Return original string for backward compatibility
        }
    }

    /**
     * Returns the end time of this event as a formatted string. For LocalDateTime objects, formats
     * using DateTimeUtil. For string-based events, returns the original string for backward
     * compatibility.
     *
     * @return the formatted end time string
     */
    public String getToAsString() {
        if (to != null) {
            return DateTimeUtil.formatDateTime(to);
        } else {
            return toString; // Return original string for backward compatibility
        }
    }

    /**
     * Returns the duration of this event as a formatted string. The format is "start_time to
     * end_time".
     *
     * @return the duration string combining start and end times
     */
    public String getDuration() {
        return getFromAsString() + " to " + getToAsString();
    }

    /**
     * Checks if this event has LocalDateTime objects (as opposed to string representation).
     *
     * @return true if this event has LocalDateTime objects, false if it uses string representation
     */
    public boolean hasDateTime() {
        return from != null && to != null;
    }

    /**
     * Returns a string representation of this event task. The format is "[E][status][priority]
     * description (from: start_time to: end_time)".
     *
     * @return the string representation of this event task
     */
    @Override
    public String toString() {
        return "[E]" + super.getStatusIcon() + super.getPriorityIcon() + " "
                + super.getDescription() + " (from: " + getFromAsString() + " to: "
                + getToAsString() + ")";
    }
}
