import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class Luffy {
    private TaskList tasks;
    private Storage storage;
    private Ui ui;

    private String numberOfTasks() {
        return tasks.getTaskCountMessage();
    }



    /**
     * Shows all deadlines and events occurring on a specific date.
     */
    private void showTasksOnDate(String dateStr) throws LuffyException {
        LocalDateTime targetDate;
        try {
            targetDate = Parser.parseDateTime(dateStr);
        } catch (LuffyException e) {
            throw new LuffyException("Invalid date format for 'due' command. " + e.getMessage());
        }

        // Get just the date part (ignore time for comparison)
        LocalDate targetDateOnly = targetDate.toLocalDate();

        ArrayList<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks.getTasks()) {
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

        // Display results
        String formattedDate = DateTimeUtil.formatDateTime(targetDate);
        if (matchingTasks.isEmpty()) {
            System.out.println("No deadlines or events found on " + formattedDate + "!");
        } else {
            System.out.println("Here are your tasks on " + formattedDate + ":");
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println((i + 1) + ". " + matchingTasks.get(i).toString());
            }
        }
    }



    private void saveTasksToFile() {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            System.out.println("OOPS!!! Couldn't save tasks to file: " + e.getMessage());
        }
    }

    private void loadTasksFromFile() {
        try {
            ArrayList<Task> loadedTasks = storage.load();
            tasks = new TaskList(loadedTasks);
        } catch (IOException e) {
            System.out.println("OOPS!!! Couldn't load tasks from file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Luffy luffy = new Luffy();
        luffy.ui = new Ui();
        luffy.storage = new Storage("data" + File.separator + "Luffy.txt");
        luffy.tasks = new TaskList();
        luffy.loadTasksFromFile(); // Load existing tasks from file at startup
        luffy.ui.showWelcome();
        String addedTask = "HAI! TASK ADDED:";

        // While scanner is open, keep asking for input
        while (luffy.ui.hasNextLine()) {
            String input = luffy.ui.readCommand();

            try {
                // If input is "bye", print goodbye message and break loop
                if (input.equals("bye") || input.equals("Bye") || input.equals("BYE")) {
                    luffy.ui.showGoodbye();
                    break;
                }

                if (input.equals("list") || input.equals("List") || input.equals("LIST")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < luffy.tasks.size(); i++) {
                        System.out.println(i + 1 + ". " + luffy.tasks.get(i).toString());
                    }
                }
                // If input starts with "todo", create a new Todo task
                else if (input.startsWith("todo") || input.startsWith("Todo")
                        || input.startsWith("TODO")) {
                    Parser.validateTodoCommand(input);
                    String description = input.substring(4).trim();
                    Todo todo = new Todo(description);
                    luffy.tasks.add(todo);
                    luffy.saveTasksToFile();
                    System.out.println(
                            addedTask + "\n" + todo.toString() + "\n" + luffy.numberOfTasks());
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

                    luffy.tasks.add(deadline);
                    luffy.saveTasksToFile();
                    System.out.println(
                            addedTask + "\n" + deadline.toString() + "\n" + luffy.numberOfTasks());
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

                    luffy.tasks.add(event);
                    luffy.saveTasksToFile();
                    System.out.println(
                            addedTask + "\n" + event.toString() + "\n" + luffy.numberOfTasks());
                } else if (input.startsWith("mark") || input.startsWith("Mark")
                        || input.startsWith("MARK")) {
                    Parser.validateMarkUnmarkCommand(input, true, luffy.tasks.size());
                    int taskNumber = Integer.parseInt(input.split(" ")[1]);
                    luffy.tasks.get(taskNumber - 1).setDone(true);
                    luffy.saveTasksToFile();
                    System.out.println(
                            "KAIZOKU! " + "\n" + luffy.tasks.get(taskNumber - 1).getStatusIcon()
                                    + " " + luffy.tasks.get(taskNumber - 1).getDescription());
                } else if (input.startsWith("unmark") || input.startsWith("Unmark")
                        || input.startsWith("UNMARK")) {
                    Parser.validateMarkUnmarkCommand(input, false, luffy.tasks.size());
                    int taskNumber = Integer.parseInt(input.split(" ")[1]);
                    luffy.tasks.get(taskNumber - 1).setDone(false);
                    luffy.saveTasksToFile();
                    System.out.println(
                            "NANI?" + "\n" + luffy.tasks.get(taskNumber - 1).getStatusIcon() + " "
                                    + luffy.tasks.get(taskNumber - 1).getDescription());
                }
                // Deleting a task
                else if (input.startsWith("delete") || input.startsWith("Delete")
                        || input.startsWith("DELETE")) {
                    Parser.validateDeleteCommand(input, luffy.tasks.size());
                    int taskNumber = Integer.parseInt(input.split(" ")[1]);
                    Task deletedTask = luffy.tasks.get(taskNumber - 1);
                    luffy.tasks.remove(taskNumber - 1);
                    luffy.saveTasksToFile();
                    System.out.println("HAI! TASK DELETED:\n" + deletedTask.toString() + "\n"
                            + luffy.numberOfTasks());
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
                    luffy.showTasksOnDate(dateStr);
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
        luffy.ui.close();
    }
}
