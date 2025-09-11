package luffy.task;

/**
 * Represents a generic task with a description, completion status, and priority level. This is the
 * base class for all task types in the Luffy task management system.
 */
public class Task {
    private String description;
    private boolean isDone;
    private Priority priority;

    /**
     * Creates a new task with the specified description. The task is initially marked as not done
     * and has NORMAL priority.
     *
     * @param description the description of the task
     */
    public Task(String description) {
        assert description != null : "Task description cannot be null";
        assert !description.trim().isEmpty() : "Task description cannot be empty";
        this.description = description;
        this.isDone = false;
        this.priority = Priority.NORMAL;
    }

    /**
     * Returns the description of this task.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this task.
     *
     * @param description the new description for the task
     */
    public void setDescription(String description) {
        assert description != null : "Task description cannot be null";
        assert !description.trim().isEmpty() : "Task description cannot be empty";
        this.description = description;
    }

    /**
     * Checks if this task is completed.
     *
     * @return true if the task is done, false otherwise
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns a string representation of the task's completion status.
     *
     * @return "[X]" if the task is done, "[ ]" if not done
     */
    public String getStatusIcon() {
        return (isDone ? "[X]" : "[ ]");
    }

    /**
     * Sets the completion status of this task.
     *
     * @param isDone true to mark the task as done, false to mark as not done
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns the priority of this task.
     *
     * @return the task priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this task.
     *
     * @param priority the new priority for the task
     */
    public void setPriority(Priority priority) {
        assert priority != null : "Task priority cannot be null";
        this.priority = priority;
    }

    /**
     * Returns a string representation of the priority for display purposes.
     *
     * @return the priority indicator (e.g., "[H]", "[N]", "[L]")
     */
    public String getPriorityIcon() {
        return "[" + priority.getShortForm() + "]";
    }
}
