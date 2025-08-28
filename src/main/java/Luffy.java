import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class Luffy {
    private TaskList tasks;
    private Storage storage;
    private Ui ui;

    public Luffy(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }



    public void run() {
        ui.showWelcome();
        String addedTask = "HAI! TASK ADDED:";

        // While scanner is open, keep asking for input
        while (ui.hasNextLine()) {
            String input = ui.readCommand();

            try {
                // If input is "bye", print goodbye message and break loop
                if (input.equals("bye") || input.equals("Bye") || input.equals("BYE")) {
                    ui.showGoodbye();
                    break;
                }

                if (input.equals("list") || input.equals("List") || input.equals("LIST")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(i + 1 + ". " + tasks.get(i).toString());
                    }
                }
                // If input starts with "todo", create a new Todo task
                else if (input.startsWith("todo") || input.startsWith("Todo")
                        || input.startsWith("TODO")) {
                    Parser.validateTodoCommand(input);
                    String description = input.substring(4).trim();
                    Todo todo = new Todo(description);
                    tasks.add(todo);
                    try {
                        storage.save(tasks.getTasks());
                    } catch (IOException e) {
                        System.out
                                .println("OOPS!!! Couldn't save tasks to file: " + e.getMessage());
                    }
                    System.out.println(addedTask + "\n" + todo.toString() + "\n"
                            + tasks.getTaskCountMessage());
                }
                // If input is "deadline <description> /by <date>", create a new Deadline task
                else if (input.startsWith("deadline") || input.startsWith("Deadline")
                        || input.startsWith("DEADLINE")) {
                    Parser.validateDeadlineCommand(input);
                    int byIndex = input.indexOf("/by");
                    String description = input.substring(8, byIndex).trim();
                    String byStr = input.substring(byIndex + 3).trim();

                    // Try to parse as date/time, fall back to string if parsing fails
                    Deadline deadline;
                    try {
                        LocalDateTime by = Parser.parseDateTime(byStr);
                        deadline = new Deadline(description, by);
                    } catch (LuffyException e) {
                        // If date parsing fails, create with string (backward compatibility)
                        deadline = new Deadline(description, byStr);
                    }

                    tasks.add(deadline);
                    try {
                        storage.save(tasks.getTasks());
                    } catch (IOException e) {
                        System.out
                                .println("OOPS!!! Couldn't save tasks to file: " + e.getMessage());
                    }
                    System.out.println(addedTask + "\n" + deadline.toString() + "\n"
                            + tasks.getTaskCountMessage());
                }
                // If input is "event <description> /from <date> /to <date>", create a new Event
                else if (input.startsWith("event") || input.startsWith("Event")
                        || input.startsWith("EVENT")) {
                    Parser.validateEventCommand(input);
                    int fromIndex = input.indexOf("/from");
                    int toIndex = input.indexOf("/to");
                    String description = input.substring(5, fromIndex).trim();
                    String fromStr = input.substring(fromIndex + 5, toIndex).trim();
                    String toStr = input.substring(toIndex + 3).trim();

                    // Try to parse as date/time, fall back to string if parsing fails
                    Event event;
                    try {
                        LocalDateTime from = Parser.parseDateTime(fromStr);
                        LocalDateTime to = Parser.parseDateTime(toStr);

                        // Validate that start time is before end time
                        Parser.validateEventTimes(from, to, fromStr, toStr);

                        event = new Event(description, from, to);
                    } catch (LuffyException e) {
                        // If date parsing fails, create with strings (backward compatibility)
                        event = new Event(description, fromStr, toStr);
                    }

                    tasks.add(event);
                    try {
                        storage.save(tasks.getTasks());
                    } catch (IOException e) {
                        System.out
                                .println("OOPS!!! Couldn't save tasks to file: " + e.getMessage());
                    }
                    System.out.println(addedTask + "\n" + event.toString() + "\n"
                            + tasks.getTaskCountMessage());
                } else if (input.startsWith("mark") || input.startsWith("Mark")
                        || input.startsWith("MARK")) {
                    Parser.validateMarkUnmarkCommand(input, true, tasks.size());
                    int taskNumber = Integer.parseInt(input.split(" ")[1]);
                    tasks.get(taskNumber - 1).setDone(true);
                    try {
                        storage.save(tasks.getTasks());
                    } catch (IOException e) {
                        System.out
                                .println("OOPS!!! Couldn't save tasks to file: " + e.getMessage());
                    }
                    System.out
                            .println("KAIZOKU! " + "\n" + tasks.get(taskNumber - 1).getStatusIcon()
                                    + " " + tasks.get(taskNumber - 1).getDescription());
                } else if (input.startsWith("unmark") || input.startsWith("Unmark")
                        || input.startsWith("UNMARK")) {
                    Parser.validateMarkUnmarkCommand(input, false, tasks.size());
                    int taskNumber = Integer.parseInt(input.split(" ")[1]);
                    tasks.get(taskNumber - 1).setDone(false);
                    try {
                        storage.save(tasks.getTasks());
                    } catch (IOException e) {
                        System.out
                                .println("OOPS!!! Couldn't save tasks to file: " + e.getMessage());
                    }
                    System.out.println("NANI?" + "\n" + tasks.get(taskNumber - 1).getStatusIcon()
                            + " " + tasks.get(taskNumber - 1).getDescription());
                }
                // Deleting a task
                else if (input.startsWith("delete") || input.startsWith("Delete")
                        || input.startsWith("DELETE")) {
                    Parser.validateDeleteCommand(input, tasks.size());
                    int taskNumber = Integer.parseInt(input.split(" ")[1]);
                    Task deletedTask = tasks.get(taskNumber - 1);
                    tasks.remove(taskNumber - 1);
                    try {
                        storage.save(tasks.getTasks());
                    } catch (IOException e) {
                        System.out
                                .println("OOPS!!! Couldn't save tasks to file: " + e.getMessage());
                    }
                    System.out.println("HAI! TASK DELETED:\n" + deletedTask.toString() + "\n"
                            + tasks.getTaskCountMessage());
                }
                // Show tasks on a specific date
                else if (input.startsWith("due") || input.startsWith("Due")
                        || input.startsWith("DUE")) {
                    String[] parts = input.split(" ", 2);
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        throw new LuffyException(
                                "Which date do you want to check? Use: due 2019-12-02");
                    }
                    String dateStr = parts[1].trim();
                    LocalDateTime targetDate;
                    try {
                        targetDate = Parser.parseDateTime(dateStr);
                    } catch (LuffyException e) {
                        throw new LuffyException(
                                "Invalid date format for 'due' command. " + e.getMessage());
                    }
                    ArrayList<Task> matchingTasks = tasks.getTasksOnDate(targetDate);
                    ui.showTasksOnDate(matchingTasks, targetDate);
                } else if (!input.isEmpty()) {
                    throw new LuffyException("I don't understand '" + input
                            + "'! Try: todo, deadline, event, mark, unmark, delete, list, due, or bye!");
                }

            } catch (LuffyException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("OOPS!!! Something went wrong! " + e.getMessage());
            }
        }
        ui.close();
    }

    public static void main(String[] args) {
        new Luffy("data" + File.separator + "Luffy.txt").run();
    }
}
