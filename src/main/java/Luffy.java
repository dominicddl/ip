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

    public static void main(String[] args) {
        Luffy luffy = new Luffy();
        System.out.println(luffy.greet);
        Scanner sc = new Scanner(System.in);

        // While scanner is open, keep asking for input
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            Task task = new Task(input);
            if (input.equals("bye") || input.equals("Bye") || input.equals("BYE")) {
                System.out.println(luffy.goodbye);
                break;
            } else if (input.equals("list") || input.equals("List") || input.equals("LIST")) {
                for (int i = 0; i < luffy.tasks.size(); i++) {
                    System.out.println(i + 1 + ". " + luffy.tasks.get(i).getStatusIcon() + " "
                            + luffy.tasks.get(i).getDescription());
                }
            } else {
                luffy.tasks.add(task);
                System.out.println("Task added: " + task.getDescription());
            }
        }
        sc.close();
    }
}
