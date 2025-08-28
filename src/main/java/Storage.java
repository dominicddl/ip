import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
}
