import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * Contains the task list and provides operations to add/delete tasks in the list.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a task from the list by index.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Gets a task by index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Gets the size of the task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Gets the underlying ArrayList for operations that need it.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns a string showing the number of tasks.
     */
    public String getTaskCountMessage() {
        return "Now you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Finds all deadlines and events occurring on a specific date.
     */
    public ArrayList<Task> getTasksOnDate(LocalDateTime targetDate) {
        // Get just the date part (ignore time for comparison)
        LocalDate targetDateOnly = targetDate.toLocalDate();

        ArrayList<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            boolean matches = false;

            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                if (deadline.hasDateTime()) {
                    LocalDate deadlineDate = deadline.getBy().toLocalDate();
                    if (deadlineDate.equals(targetDateOnly)) {
                        matches = true;
                    }
                }
            } else if (task instanceof Event) {
                Event event = (Event) task;
                if (event.hasDateTime()) {
                    LocalDate eventStartDate = event.getFrom().toLocalDate();
                    LocalDate eventEndDate = event.getTo().toLocalDate();

                    // Event matches if target date falls within the event's date range
                    if (!targetDateOnly.isBefore(eventStartDate)
                            && !targetDateOnly.isAfter(eventEndDate)) {
                        matches = true;
                    }
                }
            }

            if (matches) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }
}
