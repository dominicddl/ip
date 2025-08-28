public class Event extends Task {
    private String from;
    private String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDuration() {
        return from + " to " + to;
    }

    @Override
    public String toString() {
        return "[E]" + super.getStatusIcon() + " " + super.getDescription() + " (from: " + from
                + " to: " + to + ")";
    }
}
