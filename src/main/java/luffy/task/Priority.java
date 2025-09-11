package luffy.task;

/**
 * Represents the priority level of a task. Tasks can have different priority levels ranging from
 * LOW to HIGH, with NORMAL as the default priority.
 */
public enum Priority {
    LOW(3, "LOW", "L"), NORMAL(2, "NORMAL", "N"), HIGH(1, "HIGH", "H");

    private final int level;
    private final String displayName;
    private final String shortForm;

    /**
     * Creates a Priority with the specified level, display name, and short form.
     *
     * @param level the numeric level (lower numbers = higher priority)
     * @param displayName the full name of the priority
     * @param shortForm the short form for display
     */
    Priority(int level, String displayName, String shortForm) {
        this.level = level;
        this.displayName = displayName;
        this.shortForm = shortForm;
    }

    /**
     * Returns the numeric priority level (lower numbers = higher priority).
     *
     * @return the priority level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the display name of the priority.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the short form of the priority for compact display.
     *
     * @return the short form
     */
    public String getShortForm() {
        return shortForm;
    }

    /**
     * Parses a string into a Priority enum value. Accepts both full names and short forms.
     *
     * @param priorityString the string to parse (case-insensitive)
     * @return the corresponding Priority enum value
     * @throws IllegalArgumentException if the string doesn't match any priority
     */
    public static Priority fromString(String priorityString) {
        String input = priorityString.trim().toUpperCase();

        for (Priority priority : Priority.values()) {
            if (priority.displayName.equals(input) || priority.shortForm.equals(input)) {
                return priority;
            }
        }

        // Try parsing as numbers: 1=HIGH, 2=NORMAL, 3=LOW
        try {
            int level = Integer.parseInt(input);
            for (Priority priority : Priority.values()) {
                if (priority.level == level) {
                    return priority;
                }
            }
        } catch (NumberFormatException ignored) {
            // Not a number, continue with string matching
        }

        throw new IllegalArgumentException("Invalid priority: " + priorityString
                + ". Valid options: HIGH/H/1, NORMAL/N/2, LOW/L/3");
    }

    @Override
    public String toString() {
        return shortForm;
    }
}
