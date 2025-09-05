package luffy.command;

import java.util.ArrayList;
import luffy.task.TaskList;
import luffy.task.Task;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Command to find tasks by searching for keywords in task descriptions. Performs case-insensitive
 * substring matching and requires all keywords to be present in the task description.
 */
public class FindCommand extends Command {
    private String[] keywords;

    /**
     * Creates a new FindCommand with the specified search keywords.
     *
     * @param keywords array of keywords to search for in task descriptions
     */
    public FindCommand(String[] keywords) {
        this.keywords = keywords;
    }

    /**
     * Executes the find command by searching through all tasks for ones that contain all specified
     * keywords in their descriptions. Displays matching tasks with sequential numbering or shows a
     * "no matches" message if no tasks are found.
     *
     * @param tasks the task list to search through
     * @param ui the user interface for displaying results
     * @param storage the storage handler (not used by this command)
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> matchingTasks = new ArrayList<>();

        // Search through all tasks
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String description = task.getDescription().toLowerCase();

            // Check if all keywords are present in the description
            boolean allKeywordsMatch = true;
            for (String keyword : keywords) {
                if (!description.contains(keyword.toLowerCase())) {
                    allKeywordsMatch = false;
                    break;
                }
            }

            if (allKeywordsMatch) {
                matchingTasks.add(task);
            }
        }

        // Display results
        if (matchingTasks.isEmpty()) {
            ui.showMessage("No matching tasks found.");
        } else {
            StringBuilder result = new StringBuilder("Here are the matching tasks in your list:\n");
            for (int i = 0; i < matchingTasks.size(); i++) {
                result.append((i + 1)).append(".").append(matchingTasks.get(i).toString()).append("\n");
            }
            ui.showMessage(result.toString());
        }
    }
}
