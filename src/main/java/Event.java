import java.time.LocalDateTime;

public class Event extends Task {
    private LocalDateTime from;
    private LocalDateTime to;
    private String fromString; // For backward compatibility with old string-based data
    private String toString; // For backward compatibility with old string-based data

    // New constructor for LocalDateTime (preferred)
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
        this.fromString = null;
        this.toString = null;
    }

    // Backward compatibility constructor for String (for loading old data)
    public Event(String description, String fromString, String toString) {
        super(description);
        this.from = null;
        this.to = null;
        this.fromString = fromString;
        this.toString = toString;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public String getFromAsString() {
        if (from != null) {
            return DateTimeUtil.formatDateTime(from);
        } else {
            return fromString; // Return original string for backward compatibility
        }
    }

    public String getToAsString() {
        if (to != null) {
            return DateTimeUtil.formatDateTime(to);
        } else {
            return toString; // Return original string for backward compatibility
        }
    }

    public String getDuration() {
        return getFromAsString() + " to " + getToAsString();
    }

    public boolean hasDateTime() {
        return from != null && to != null;
    }

    @Override
    public String toString() {
        return "[E]" + super.getStatusIcon() + " " + super.getDescription() + " (from: "
                + getFromAsString() + " to: " + getToAsString() + ")";
    }
}
