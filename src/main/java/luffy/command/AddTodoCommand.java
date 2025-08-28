package luffy.command;

import java.io.IOException;
import luffy.task.TaskList;
import luffy.task.Todo;
import luffy.ui.Ui;
import luffy.storage.Storage;

/**
 * Command to add a todo task.
 */
public class AddTodoCommand extends Command {
    private String description;

    public AddTodoCommand(String description) {
        this.description = description;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        Todo todo = new Todo(description);
        tasks.add(todo);
        storage.save(tasks.getTasks());
        System.out.println(
                "HAI! TASK ADDED:\n" + todo.toString() + "\n" + tasks.getTaskCountMessage());
    }
}
