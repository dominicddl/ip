import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Luffy {
    String greet = "Hello! I'm Luffy\n" + "Be my crewmate!";
    String goodbye = "Bye! See you next time!\n" + "I'll be waiting for you to join my crew!\n";

    ArrayList<Task> tasks = new ArrayList<>();
    private static final String DATA_FILE_PATH = "data" + File.separator + "Luffy.txt";

    private String numberOfTasks() {
        return "Now you have " + tasks.size() + " tasks in the list.";
    }

    private void validateTodoCommand(String input) throws LuffyException {
        if (input.trim().length() <= 4 || input.substring(4).trim().isEmpty()) {
            throw new LuffyException(
                    "Hey! A todo task needs a description, you know! I can't remember nothing!");
        }
    }

    private void validateDeadlineCommand(String input) throws LuffyException {
        if (input.trim().length() <= 8 || input.substring(8).trim().isEmpty()) {
            throw new LuffyException(
                    "Oi! You gotta tell me what the deadline is for! I'm not a mind reader!");
        }

        int byIndex = input.indexOf("/by");
        if (byIndex == -1) {
            throw new LuffyException(
                    "Where's the '/by' part? I need to know when this deadline is, dattebayo!");
        }

        if (byIndex + 3 >= input.length() || input.substring(byIndex + 3).trim().isEmpty()) {
            throw new LuffyException(
                    "You forgot to tell me WHEN the deadline is! Put something after '/by'!");
        }
    }

    private void validateEventCommand(String input) throws LuffyException {
        if (input.trim().length() <= 5 || input.substring(5).trim().isEmpty()) {
            throw new LuffyException(
                    "What event are we talking about? Give me a description, nakama!");
        }

        int fromIndex = input.indexOf("/from");
        int toIndex = input.indexOf("/to");

        if (fromIndex == -1) {
            throw new LuffyException(
                    "Missing '/from'! When does this event start? I need to know!");
        }

        if (toIndex == -1) {
            throw new LuffyException(
                    "Missing '/to'! When does this event end? Don't leave me hanging!");
        }

        if (fromIndex >= toIndex) {
            throw new LuffyException(
                    "Hey! '/from' should come before '/to'! That's just common sense!");
        }

        if (fromIndex + 5 >= input.length()
                || input.substring(fromIndex + 5, toIndex).trim().isEmpty()) {
            throw new LuffyException(
                    "You didn't tell me when it starts! Put something after '/from'!");
        }

        if (toIndex + 3 >= input.length() || input.substring(toIndex + 3).trim().isEmpty()) {
            throw new LuffyException("You didn't tell me when it ends! Put something after '/to'!");
        }
    }

    private void validateMarkUnmarkCommand(String input, boolean isMark) throws LuffyException {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            String action = isMark ? "mark" : "unmark";
            throw new LuffyException(
                    "Which task do you want me to " + action + "? Give me a number!");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new LuffyException("Task " + taskNumber + "? That doesn't exist! I only have "
                        + tasks.size() + " tasks!");
            }
        } catch (NumberFormatException e) {
            throw new LuffyException(
                    "'" + parts[1] + "' is not a number! Give me a proper task number!");
        }
    }

    private void validateDeleteCommand(String input) throws LuffyException {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            throw new LuffyException("Which task do you want me to delete? Give me a number!");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new LuffyException("Task " + taskNumber + "? That doesn't exist! I only have "
                        + tasks.size() + " tasks!");
            }
        } catch (NumberFormatException e) {
            throw new LuffyException(
                    "'" + parts[1] + "' is not a number! Give me a proper task number!");
        }
    }

    /**
     * Parses a date/time string into LocalDateTime. Supports formats: - yyyy-mm-dd (date only, time
     * defaults to 23:59) - yyyy-mm-dd HHmm (date with time in 24-hour format) - yyyy-mm-dd HH:mm
     * (date with time in 24-hour format) - yyyy-mm-dd h:mm AM/PM (date with time in 12-hour format)
     * - d/m/yyyy (date only, time defaults to 23:59) - d/m/yyyy HHmm (date with time in 24-hour
     * format) - d/m/yyyy HH:mm (date with time in 24-hour format) - d/m/yyyy h:mm AM/PM (date with
     * time in 12-hour format)
     */
    private LocalDateTime parseDateTime(String dateTimeStr) throws LuffyException {
        dateTimeStr = dateTimeStr.trim();

        // Define possible formatters
        DateTimeFormatter[] formatters = {
                // yyyy-mm-dd formats
                DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                // d/m/yyyy formats
                DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
                DateTimeFormatter.ofPattern("d/M/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("d/M/yyyy h:mm a"),
                DateTimeFormatter.ofPattern("d/M/yyyy")};

        // Try parsing with each formatter
        for (DateTimeFormatter formatter : formatters) {
            try {
                if (dateTimeStr.matches(".*\\d{4}-\\d{2}-\\d{2}$")
                        || dateTimeStr.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}$")) {
                    // Date only formats - parse as LocalDate and set time to 23:59
                    LocalDate date = LocalDate.parse(dateTimeStr, formatter);
                    return date.atTime(23, 59);
                } else {
                    // Date with time formats
                    return LocalDateTime.parse(dateTimeStr, formatter);
                }
            } catch (DateTimeParseException e) {
                // Continue to next formatter
            }
        }

        // If no formatter worked, throw exception
        throw new LuffyException("Invalid date/time format: '" + dateTimeStr
                + "'. Use formats like: 2019-12-02, 2019-12-02 1800, 2019-12-02 18:00, 2019-12-02 6:00 PM, "
                + "2/12/2019, 2/12/2019 1800, 2/12/2019 18:00, or 2/12/2019 6:00 PM");
    }



    private void saveTasksToFile() {
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            FileWriter writer = new FileWriter(DATA_FILE_PATH);
            for (Task task : tasks) {
                String line = "";
                int status = task.isDone() ? 1 : 0;

                if (task instanceof Todo) {
                    line = "T | " + status + " | " + task.getDescription();
                } else if (task instanceof Deadline) {
                    Deadline deadline = (Deadline) task;
                    line = "D | " + status + " | " + task.getDescription() + " | "
                            + deadline.getBy();
                } else if (task instanceof Event) {
                    Event event = (Event) task;
                    line = "E | " + status + " | " + task.getDescription() + " | "
                            + event.getDuration();
                }

                writer.write(line + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("OOPS!!! Couldn't save tasks to file: " + e.getMessage());
        }
    }

    private void loadTasksFromFile() {
        File file = new File(DATA_FILE_PATH);
        if (!file.exists()) {
            return; // No file to load from, start with empty task list
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE_PATH));
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }

                try {
                    String[] parts = line.split(" \\| ");
                    if (parts.length < 3) {
                        System.out.println(
                                "OOPS!!! Corrupted data found at line " + lineNumber + ": " + line);
                        continue;
                    }

                    String taskType = parts[0].trim();
                    int status = Integer.parseInt(parts[1].trim());
                    String description = parts[2].trim();

                    Task task = null;

                    if (taskType.equals("T")) {
                        if (parts.length != 3) {
                            System.out.println("OOPS!!! Corrupted Todo data at line " + lineNumber
                                    + ": " + line);
                            continue;
                        }
                        task = new Todo(description);
                    } else if (taskType.equals("D")) {
                        if (parts.length != 4) {
                            System.out.println("OOPS!!! Corrupted Deadline data at line "
                                    + lineNumber + ": " + line);
                            continue;
                        }
                        String by = parts[3].trim();
                        task = new Deadline(description, by);
                    } else if (taskType.equals("E")) {
                        if (parts.length != 4) {
                            System.out.println("OOPS!!! Corrupted Event data at line " + lineNumber
                                    + ": " + line);
                            continue;
                        }
                        String duration = parts[3].trim();
                        // Parse duration back to from and to
                        String[] durationParts = duration.split(" to ");
                        if (durationParts.length != 2) {
                            System.out.println("OOPS!!! Corrupted Event duration at line "
                                    + lineNumber + ": " + line);
                            continue;
                        }
                        task = new Event(description, durationParts[0], durationParts[1]);
                    } else {
                        System.out.println(
                                "OOPS!!! Unknown task type at line " + lineNumber + ": " + line);
                        continue;
                    }

                    if (task != null) {
                        task.setDone(status == 1);
                        tasks.add(task);
                    }

                } catch (NumberFormatException e) {
                    System.out.println(
                            "OOPS!!! Invalid status format at line " + lineNumber + ": " + line);
                } catch (Exception e) {
                    System.out.println("OOPS!!! Error parsing line " + lineNumber + ": " + line
                            + " - " + e.getMessage());
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("OOPS!!! Couldn't load tasks from file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Luffy luffy = new Luffy();
        luffy.loadTasksFromFile(); // Load existing tasks from file at startup
        System.out.println(luffy.greet);
        Scanner sc = new Scanner(System.in);
        String addedTask = "HAI! TASK ADDED:";

        // While scanner is open, keep asking for input
        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();

            try {
                // If input is "bye", print goodbye message and break loop
                if (input.equals("bye") || input.equals("Bye") || input.equals("BYE")) {
                    System.out.println(luffy.goodbye);
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
                    luffy.validateTodoCommand(input);
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
                    luffy.validateDeadlineCommand(input);
                    int byIndex = input.indexOf("/by");
                    String description = input.substring(8, byIndex).trim();
                    String by = input.substring(byIndex + 3).trim();
                    Deadline deadline = new Deadline(description, by);
                    luffy.tasks.add(deadline);
                    luffy.saveTasksToFile();
                    System.out.println(
                            addedTask + "\n" + deadline.toString() + "\n" + luffy.numberOfTasks());
                }
                // If input is "event <description> /from <date> /to <date>", create a new Event
                else if (input.startsWith("event") || input.startsWith("Event")
                        || input.startsWith("EVENT")) {
                    luffy.validateEventCommand(input);
                    int fromIndex = input.indexOf("/from");
                    int toIndex = input.indexOf("/to");
                    String description = input.substring(5, fromIndex).trim();
                    String from = input.substring(fromIndex + 5, toIndex).trim();
                    String to = input.substring(toIndex + 3).trim();
                    Event event = new Event(description, from, to);
                    luffy.tasks.add(event);
                    luffy.saveTasksToFile();
                    System.out.println(
                            addedTask + "\n" + event.toString() + "\n" + luffy.numberOfTasks());
                } else if (input.startsWith("mark") || input.startsWith("Mark")
                        || input.startsWith("MARK")) {
                    luffy.validateMarkUnmarkCommand(input, true);
                    int taskNumber = Integer.parseInt(input.split(" ")[1]);
                    luffy.tasks.get(taskNumber - 1).setDone(true);
                    luffy.saveTasksToFile();
                    System.out.println(
                            "KAIZOKU! " + "\n" + luffy.tasks.get(taskNumber - 1).getStatusIcon()
                                    + " " + luffy.tasks.get(taskNumber - 1).getDescription());
                } else if (input.startsWith("unmark") || input.startsWith("Unmark")
                        || input.startsWith("UNMARK")) {
                    luffy.validateMarkUnmarkCommand(input, false);
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
                    luffy.validateDeleteCommand(input);
                    int taskNumber = Integer.parseInt(input.split(" ")[1]);
                    Task deletedTask = luffy.tasks.get(taskNumber - 1);
                    luffy.tasks.remove(taskNumber - 1);
                    luffy.saveTasksToFile();
                    System.out.println("HAI! TASK DELETED:\n" + deletedTask.toString() + "\n"
                            + luffy.numberOfTasks());
                } else if (!input.isEmpty()) {
                    throw new LuffyException("I don't understand '" + input
                            + "'! Try: todo, deadline, event, mark, unmark, delete, list, or bye!");
                }

            } catch (LuffyException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("OOPS!!! Something went wrong! " + e.getMessage());
            }
        }
        sc.close();
    }
}
