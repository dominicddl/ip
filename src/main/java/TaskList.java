import java.util.ArrayList;

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
}
