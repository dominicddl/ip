import java.util.Scanner;
import java.util.ArrayList;

public class Luffy {
    String greet = "____________________________________________________________\n"
            + "Hello! I'm Luffy\n" + "Do you want to be my crewmate?\n"
            + "____________________________________________________________\n";
    String goodbye = "____________________________________________________________\n"
            + "Bye! See you next time!\n" + "I'll be waiting for you to join my crew!\n"
            + "____________________________________________________________\n";
    ArrayList<String> tasks = new ArrayList<>();

    public static void main(String[] args) {
        Luffy luffy = new Luffy();
        System.out.println(luffy.greet);
        Scanner sc = new Scanner(System.in);

        // While scanner is open, keep asking for input
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            if (input.equals("bye") || input.equals("Bye") || input.equals("BYE")) {
                System.out.println(luffy.goodbye);
                break;
            } else {
                luffy.tasks.add(input);
                System.out.println("Task added: " + input);
            }
        }
        sc.close();
    }
}
