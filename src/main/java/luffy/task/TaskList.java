package luffy.task;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * Contains the task list and provides operations to add/delete tasks in the list. This class
 * manages a collection of Task objects and provides methods to manipulate and query the collection.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates a new empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a new task list with the specified list of tasks.
     *
     * @param tasks the initial list of tasks
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the task list.
     *
     * @param task the task to add to the list
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a task from the list at the specified index.
     *
     * @param index the index of the task to remove
     * @return the task that was removed from the list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified index in the list.
     *
     * @param index the index of the task to return
     * @return the task at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks in the list
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying ArrayList containing all tasks. This method is provided for operations
     * that need direct access to the list.
     *
     * @return the ArrayList containing all tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns a formatted message indicating the number of tasks in the list.
     *
     * @return a string message showing the task count
     */
    public String getTaskCountMessage() {
        return "Now you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Finds all deadline and event tasks that occur on the specified date. For deadlines, matches
     * if the due date falls on the target date. For events, matches if the target date falls within
     * the event's date range. Only considers tasks with LocalDateTime objects (ignores string-based
     * tasks).
     *
     * @param targetDate the date to search for (time component is ignored)
     * @return a list of tasks that occur on the specified date
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
