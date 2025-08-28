package luffy.task;

import java.time.LocalDateTime;
import luffy.util.DateTimeUtil;

public class Deadline extends Task {
    private LocalDateTime by;
    private String byString; // For backward compatibility with old string-based data

    // New constructor for LocalDateTime (preferred)
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
        this.byString = null;
    }

    // Backward compatibility constructor for String (for loading old data)
    public Deadline(String description, String byString) {
        super(description);
        this.by = null;
        this.byString = byString;
    }

    public LocalDateTime getBy() {
        return by;
    }

    public String getByAsString() {
        if (by != null) {
            return DateTimeUtil.formatDateTime(by);
        } else {
            return byString; // Return original string for backward compatibility
        }
    }

    public boolean hasDateTime() {
        return by != null;
    }

    @Override
    public String toString() {
        return "[D]" + super.getStatusIcon() + " " + super.getDescription() + " (by: "
                + getByAsString() + ")";
    }
}
