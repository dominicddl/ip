package luffy.command;

import java.io.IOException;
import luffy.task.TaskList;
import luffy.task.Todo;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Command to add a todo task to the task list. Todo tasks are simple tasks with just a description
 * and completion status.
 */
public class AddTodoCommand extends Command {
    private String description;

    /**
     * Creates a new AddTodoCommand with the specified task description.
     *
     * @param description the description of the todo task to add
     */
    public AddTodoCommand(String description) {
        this.description = description;
    }

    /**
     * Executes the command by creating a new Todo task, adding it to the task list, saving the
     * updated list to storage, and displaying a confirmation message.
     *
     * @param tasks the task list to add the todo to
     * @param ui the user interface for displaying messages
     * @param storage the storage handler for saving changes
     * @throws IOException if there is an error saving to storage
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        Todo todo = new Todo(description);
        tasks.add(todo);
        storage.save(tasks.getTasks());
        System.out.println(
                "HAI! TASK ADDED:\n" + todo.toString() + "\n" + tasks.getTaskCountMessage());
    }
}
