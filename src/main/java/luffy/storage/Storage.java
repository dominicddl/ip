package luffy.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import luffy.task.Task;
import luffy.task.Todo;
import luffy.task.Deadline;
import luffy.task.Event;
import luffy.util.DateTimeUtil;

/**
 * Handles the loading and saving of tasks to and from the file.
 */
public class Storage {
    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    /**
     * Saves the task list to the file.
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        // Create data directory if it doesn't exist
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        FileWriter writer = new FileWriter(filePath);
        for (Task task : tasks) {
            String line = "";
            int status = task.isDone() ? 1 : 0;

            if (task instanceof Todo) {
                line = "T | " + status + " | " + task.getDescription();
            } else if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                if (deadline.hasDateTime()) {
                    // Save LocalDateTime in ISO format for new data
                    line = "D | " + status + " | " + task.getDescription() + " | "
                            + DateTimeUtil.formatDateTimeForFile(deadline.getBy());
                } else {
                    // Save as string for backward compatibility
                    line = "D | " + status + " | " + task.getDescription() + " | "
                            + deadline.getByAsString();
                }
            } else if (task instanceof Event) {
                Event event = (Event) task;
                if (event.hasDateTime()) {
                    // Save LocalDateTime in ISO format for new data (separate from and to
                    // fields)
                    line = "E | " + status + " | " + task.getDescription() + " | "
                            + DateTimeUtil.formatDateTimeForFile(event.getFrom()) + " | "
                            + DateTimeUtil.formatDateTimeForFile(event.getTo());
                } else {
                    // Save as combined string for backward compatibility
                    line = "E | " + status + " | " + task.getDescription() + " | "
                            + event.getDuration();
                }
            }

            writer.write(line + System.lineSeparator());
        }
        writer.close();
    }

    /**
     * Loads tasks from the file.
     * 
     * @return ArrayList of tasks loaded from file
     * @throws IOException if file cannot be read
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks; // Return empty list if file doesn't exist
        }

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
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
                        System.out.println(
                                "OOPS!!! Corrupted Todo data at line " + lineNumber + ": " + line);
                        continue;
                    }
                    task = new Todo(description);
                } else if (taskType.equals("D")) {
                    if (parts.length != 4) {
                        System.out.println("OOPS!!! Corrupted Deadline data at line " + lineNumber
                                + ": " + line);
                        continue;
                    }
                    String byString = parts[3].trim();

                    // Try to parse as ISO LocalDateTime first (new format)
                    try {
                        LocalDateTime by = DateTimeUtil.parseDateTimeFromFile(byString);
                        task = new Deadline(description, by);
                    } catch (Exception e) {
                        // If ISO parsing fails, treat as old string format
                        task = new Deadline(description, byString);
                    }
                } else if (taskType.equals("E")) {
                    if (parts.length == 5) {
                        // New format: E | status | description | from_iso | to_iso
                        String fromString = parts[3].trim();
                        String toString = parts[4].trim();

                        try {
                            LocalDateTime from = DateTimeUtil.parseDateTimeFromFile(fromString);
                            LocalDateTime to = DateTimeUtil.parseDateTimeFromFile(toString);
                            task = new Event(description, from, to);
                        } catch (Exception e) {
                            System.out.println("OOPS!!! Invalid date format in Event at line "
                                    + lineNumber + ": " + line);
                            continue;
                        }
                    } else if (parts.length == 4) {
                        // Old format: E | status | description | duration
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
                                "OOPS!!! Corrupted Event data at line " + lineNumber + ": " + line);
                        continue;
                    }
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
                System.out.println("OOPS!!! Error parsing line " + lineNumber + ": " + line + " - "
                        + e.getMessage());
            }
        }
        reader.close();
        return tasks;
    }
}
