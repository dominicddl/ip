package luffy.ui;

import java.util.ArrayList;
import java.time.LocalDateTime;
import luffy.task.Task;
import luffy.task.TaskList;
import luffy.task.Priority;
import luffy.util.DateTimeUtil;

/**
 * GUI-friendly UI implementation that captures output instead of printing to console. This class is
 * used by the JavaFX GUI to get response strings from command execution.
 */
public class GuiUi extends Ui {
    private StringBuilder response;

    /**
     * Creates a new GuiUi instance and initializes the response builder.
     */
    public GuiUi() {
        super();
        this.response = new StringBuilder();
    }

    /**
     * Displays the welcome message by capturing it in the response.
     */
    @Override
    public void showWelcome() {
        response.append("Hello! I'm Luffy\nBe my crewmate!");
    }

    /**
     * Displays the goodbye message by capturing it in the response.
     */
    @Override
    public void showGoodbye() {
        response.append("Bye! See you next time!\nI'll be waiting for you to join my crew!");
    }

    /**
     * Not applicable for GUI - user input is handled by JavaFX components.
     *
     * @return empty string
     */
    @Override
    public String readCommand() {
        return "";
    }

    /**
     * Not applicable for GUI.
     *
     * @return false
     */
    @Override
    public boolean hasNextLine() {
        return false;
    }

    /**
     * Captures loading error message in the response.
     */
    @Override
    public void showLoadingError() {
        response.append("OOPS!!! Couldn't load tasks from file. Starting with empty task list.");
    }

    /**
     * GUI doesn't need divider lines - they're handled by the chat interface design.
     */
    @Override
    public void showLine() {
        // No divider lines needed in GUI
    }

    /**
     * Captures error message in the response.
     *
     * @param message the error message to capture
     */
    @Override
    public void showError(String message) {
        response.append(message);
    }

    /**
     * Captures tasks on date information in the response.
     *
     * @param matchingTasks the list of tasks that occur on the specified date
     * @param targetDate the date being queried
     */
    @Override
    public void showTasksOnDate(ArrayList<Task> matchingTasks, LocalDateTime targetDate) {
        String formattedDate = DateTimeUtil.formatDateTime(targetDate);
        if (matchingTasks.isEmpty()) {
            response.append("No deadlines or events found on ").append(formattedDate).append("!");
        } else {
            response.append("Here are your tasks on ").append(formattedDate).append(":\n");
            for (int i = 0; i < matchingTasks.size(); i++) {
                response.append((i + 1)).append(". ").append(matchingTasks.get(i).toString())
                        .append("\n");
            }
        }
    }

    /**
     * Not applicable for GUI.
     */
    @Override
    public void close() {
        // No scanner to close in GUI
    }

    /**
     * Captures task addition confirmation message in the response.
     *
     * @param taskString the string representation of the added task
     * @param taskCountMessage the message showing current task count
     */
    @Override
    public void showTaskAdded(String taskString, String taskCountMessage) {
        response.append("HAI! TASK ADDED:\n").append(taskString).append("\n")
                .append(taskCountMessage);
    }

    /**
     * Captures the task list in the response.
     *
     * @param tasks the task list to display
     */
    @Override
    public void showTaskList(TaskList tasks) {
        response.append("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            response.append((i + 1)).append(". ").append(tasks.get(i).toString()).append("\n");
        }
    }

    /**
     * Captures task deletion confirmation message in the response.
     *
     * @param taskString the string representation of the deleted task
     * @param taskCountMessage the message showing current task count
     */
    @Override
    public void showTaskDeleted(String taskString, String taskCountMessage) {
        response.append("HAI! TASK DELETED:\n").append(taskString).append("\n")
                .append(taskCountMessage);
    }

    /**
     * Captures task marking confirmation message in the response.
     *
     * @param taskString the string representation of the marked task
     */
    @Override
    public void showTaskMarked(String taskString) {
        response.append("KAIZOKU!\n").append(taskString);
    }

    /**
     * Captures task unmarking confirmation message in the response.
     *
     * @param taskString the string representation of the unmarked task
     */
    @Override
    public void showTaskUnmarked(String taskString) {
        response.append("NANI?\n").append(taskString);
    }

    /**
     * Captures a general message in the response.
     *
     * @param message the message to capture
     */
    @Override
    public void showMessage(String message) {
        response.append(message);
    }

    @Override
    public void showPriorityChanged(String taskString, Priority oldPriority, Priority newPriority) {
        response.append("YOSH! I've changed the priority of this task from ")
                .append(oldPriority.getDisplayName()).append(" to ")
                .append(newPriority.getDisplayName()).append(":\n").append(taskString);
    }

    /**
     * Gets the captured response string and clears the internal buffer.
     *
     * @return the captured response as a string
     */
    public String getResponse() {
        String result = response.toString();
        response.setLength(0); // Clear the buffer for next use
        return result;
    }
}
