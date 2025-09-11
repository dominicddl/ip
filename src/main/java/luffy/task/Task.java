package luffy.task;

/**
 * Represents a generic task with a description and completion status. This is the base class for
 * all task types in the Luffy task management system.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Creates a new task with the specified description. The task is initially marked as not done.
     *
     * @param description the description of the task
     */
    public Task(String description) {
        assert description != null : "Task description cannot be null";
        assert !description.trim().isEmpty() : "Task description cannot be empty";
        this.description = description;
        this.isDone = false;
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
}
