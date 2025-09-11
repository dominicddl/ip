package luffy.task;

/**
 * Represents a todo task, which is a simple task with just a description. Todo tasks are displayed
 * with a "[T]" prefix.
 */
public class Todo extends Task {

    /**
     * Creates a new todo task with the specified description.
     *
     * @param description the description of the todo task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a string representation of this todo task. The format is "[T][status][priority]
     * description" where status is either X or space and priority is H/N/L.
     *
     * @return the string representation of this todo task
     */
    @Override
    public String toString() {
        return "[T]" + super.getStatusIcon() + super.getPriorityIcon() + " "
                + super.getDescription();
    }
}
