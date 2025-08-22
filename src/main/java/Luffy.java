import java.util.Scanner;
import java.util.ArrayList;

public class Luffy {
    String greet = "____________________________________________________________\n"
            + "Hello! I'm Luffy\n" + "Be my crewmate!\n"
            + "____________________________________________________________\n";
    String goodbye = "____________________________________________________________\n"
            + "Bye! See you next time!\n" + "I'll be waiting for you to join my crew!\n"
            + "____________________________________________________________\n";
    ArrayList<Task> tasks = new ArrayList<>();

    private String numberOfTasks() {
        return "Now you have " + tasks.size() + " tasks in the list.";
    }

    public static void main(String[] args) {
        Luffy luffy = new Luffy();
        System.out.println(luffy.greet);
        Scanner sc = new Scanner(System.in);
        String addedTask = "HAI! TASK ADDED:";

        // While scanner is open, keep asking for input
        while (sc.hasNextLine()) {
            String input = sc.nextLine();

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
            if (input.startsWith("todo") || input.startsWith("Todo") || input.startsWith("TODO")) {
                String description = input.substring(5);
                Todo todo = new Todo(description);
                luffy.tasks.add(todo);
                System.out
                        .println(addedTask + "\n" + todo.toString() + "\n" + luffy.numberOfTasks());
            }

            // If input is "deadline <description> /by <date>", create a new Deadline task
            if (input.startsWith("deadline") || input.startsWith("Deadline")
                    || input.startsWith("DEADLINE")) {
                int byIndex = input.indexOf("/by");
                if (byIndex != -1) {
                    String description = input.substring(8, byIndex).trim();
                    String by = input.substring(byIndex + 3).trim();
                    Deadline deadline = new Deadline(description, by);
                    luffy.tasks.add(deadline);
                    System.out.println(
                            addedTask + "\n" + deadline.toString() + "\n" + luffy.numberOfTasks());
                }
            }

            // If input is "event <description> /from <date> /to <date>", create a new Event task
            if (input.startsWith("event") || input.startsWith("Event")
                    || input.startsWith("EVENT")) {
                int fromIndex = input.indexOf("/from");
                int toIndex = input.indexOf("/to");
                if (fromIndex != -1 && toIndex != -1 && fromIndex < toIndex) {
                    String description = input.substring(6, fromIndex).trim();
                    String from = input.substring(fromIndex + 5, toIndex).trim();
                    String to = input.substring(toIndex + 3).trim();
                    Event event = new Event(description, from, to);
                    luffy.tasks.add(event);
                    System.out.println(
                            addedTask + "\n" + event.toString() + "\n" + luffy.numberOfTasks());
                }
            }

            if (input.startsWith("mark") || input.startsWith("Mark") || input.startsWith("MARK")) {
                int taskNumber = Integer.parseInt(input.split(" ")[1]);
                luffy.tasks.get(taskNumber - 1).setDone(true);
                System.out.println(
                        "KAIZOKU! " + "\n" + luffy.tasks.get(taskNumber - 1).getStatusIcon() + " "
                                + luffy.tasks.get(taskNumber - 1).getDescription());
            } else if (input.startsWith("unmark") || input.startsWith("Unmark")
                    || input.startsWith("UNMARK")) {
                int taskNumber = Integer.parseInt(input.split(" ")[1]);
                luffy.tasks.get(taskNumber - 1).setDone(false);
                System.out.println("NANI?" + "\n" + luffy.tasks.get(taskNumber - 1).getStatusIcon()
                        + " " + luffy.tasks.get(taskNumber - 1).getDescription());
            }
        }
        sc.close();
    }
}
