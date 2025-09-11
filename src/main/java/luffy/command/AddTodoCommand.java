package luffy.command;

import luffy.exception.LuffyException;
import luffy.task.Task;
import luffy.task.Todo;

/**
 * Command to add a todo task to the task list. Todo tasks are simple tasks with just a description
 * and completion status.
 */
public class AddTodoCommand extends AddTaskCommand {
    private String description;

    /**
     * Creates a new AddTodoCommand with the specified task description.
     *
     * @param description the description of the todo task to add
     */
    public AddTodoCommand(String description) {
        this.description = description;
    }

    @Override
    protected Task createTask() throws LuffyException {
        return new Todo(description);
    }
}
