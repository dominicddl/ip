package luffy.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import luffy.task.Task;
import luffy.task.Todo;
import luffy.task.Deadline;
import luffy.task.Event;

public class StorageTest {
    private Storage storage;
    private String testFilePath;
    private File testFile;

    @BeforeEach
    public void setUp() {
        testFilePath = "test_data.txt";
        testFile = new File(testFilePath);
        storage = new Storage(testFilePath);
    }

    @AfterEach
    public void tearDown() {
        // Clean up test files
        if (testFile.exists()) {
            testFile.delete();
        }
        // Clean up data directory if it was created during tests
        File dataDir = new File("data");
        if (dataDir.exists() && dataDir.list().length == 0) {
            dataDir.delete();
        }
    }

    // Tests for constructor and getFilePath
    @Test
    public void constructor_validFilePath_setsFilePath() {
        Storage storage = new Storage("test/path.txt");
        assertEquals("test/path.txt", storage.getFilePath());
    }

    @Test
    public void getFilePath_returnsCorrectPath() {
        assertEquals(testFilePath, storage.getFilePath());
    }

    // Tests for save method
    @Test
    public void save_emptyTaskList_createsEmptyFile() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        storage.save(tasks);

        assertTrue(testFile.exists());
        assertEquals(0, testFile.length());
    }

    @Test
    public void save_todoTask_savesCorrectFormat() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        tasks.add(todo);

        storage.save(tasks);

        assertTrue(testFile.exists());
        // Read and verify content
        Storage loadStorage = new Storage(testFilePath);
        ArrayList<Task> loadedTasks = loadStorage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Todo);
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertFalse(loadedTasks.get(0).isDone());
    }

    @Test
    public void save_completedTodoTask_savesCorrectStatus() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.setDone(true);
        tasks.add(todo);

        storage.save(tasks);

        Storage loadStorage = new Storage(testFilePath);
        ArrayList<Task> loadedTasks = loadStorage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0).isDone());
    }

    @Test
    public void save_deadlineTaskWithDateTime_savesCorrectFormat() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        LocalDateTime deadline = LocalDateTime.of(2024, 12, 15, 14, 30);
        Deadline deadlineTask = new Deadline("return book", deadline);
        tasks.add(deadlineTask);

        storage.save(tasks);

        Storage loadStorage = new Storage(testFilePath);
        ArrayList<Task> loadedTasks = loadStorage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Deadline);
        assertEquals("return book", loadedTasks.get(0).getDescription());

        Deadline loadedDeadline = (Deadline) loadedTasks.get(0);
        assertTrue(loadedDeadline.hasDateTime());
        assertEquals(deadline, loadedDeadline.getBy());
    }

    @Test
    public void save_deadlineTaskWithString_savesCorrectFormat() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        Deadline deadlineTask = new Deadline("return book", "Monday");
        tasks.add(deadlineTask);

        storage.save(tasks);

        Storage loadStorage = new Storage(testFilePath);
        ArrayList<Task> loadedTasks = loadStorage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Deadline);

        Deadline loadedDeadline = (Deadline) loadedTasks.get(0);
        assertFalse(loadedDeadline.hasDateTime());
        assertEquals("Monday", loadedDeadline.getByAsString());
    }

    @Test
    public void save_eventTaskWithDateTime_savesCorrectFormat() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        LocalDateTime from = LocalDateTime.of(2024, 12, 15, 10, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 15, 12, 0);
        Event eventTask = new Event("meeting", from, to);
        tasks.add(eventTask);

        storage.save(tasks);

        Storage loadStorage = new Storage(testFilePath);
        ArrayList<Task> loadedTasks = loadStorage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Event);

        Event loadedEvent = (Event) loadedTasks.get(0);
        assertTrue(loadedEvent.hasDateTime());
        assertEquals(from, loadedEvent.getFrom());
        assertEquals(to, loadedEvent.getTo());
    }

    @Test
    public void save_eventTaskWithString_savesCorrectFormat() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        Event eventTask = new Event("meeting", "Monday 10am", "Monday 12pm");
        tasks.add(eventTask);

        storage.save(tasks);

        Storage loadStorage = new Storage(testFilePath);
        ArrayList<Task> loadedTasks = loadStorage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Event);

        Event loadedEvent = (Event) loadedTasks.get(0);
        assertFalse(loadedEvent.hasDateTime());
        assertEquals("Monday 10am to Monday 12pm", loadedEvent.getDuration());
    }

    @Test
    public void save_multipleTasks_savesAllTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("task 1"));
        tasks.add(new Deadline("task 2", "Monday"));
        tasks.add(new Event("task 3", "Mon 10am", "Mon 12pm"));

        storage.save(tasks);

        Storage loadStorage = new Storage(testFilePath);
        ArrayList<Task> loadedTasks = loadStorage.load();
        assertEquals(3, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Todo);
        assertTrue(loadedTasks.get(1) instanceof Deadline);
        assertTrue(loadedTasks.get(2) instanceof Event);
    }

    // Tests for load method
    @Test
    public void load_nonExistentFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage("non_existent_file.txt");
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_emptyFile_returnsEmptyList() throws IOException {
        // Create empty file
        testFile.createNewFile();

        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_validTodoLine_loadsTodoCorrectly() throws IOException {
        // Create test file with todo
        FileWriter writer = new FileWriter(testFile);
        writer.write("T | 0 | NORMAL | read book\n");
        writer.close();

        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof Todo);
        assertEquals("read book", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void load_validCompletedTodoLine_loadsTodoCorrectly() throws IOException {
        FileWriter writer = new FileWriter(testFile);
        writer.write("T | 1 | NORMAL | read book\n");
        writer.close();

        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void load_validDeadlineWithDateTime_loadsDeadlineCorrectly() throws IOException {
        FileWriter writer = new FileWriter(testFile);
        writer.write("D | 0 | NORMAL | return book | 2024-12-15T14:30:00\n");
        writer.close();

        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof Deadline);

        Deadline deadline = (Deadline) tasks.get(0);
        assertTrue(deadline.hasDateTime());
        assertEquals(LocalDateTime.of(2024, 12, 15, 14, 30), deadline.getBy());
    }

    @Test
    public void load_validDeadlineWithString_loadsDeadlineCorrectly() throws IOException {
        FileWriter writer = new FileWriter(testFile);
        writer.write("D | 0 | NORMAL | return book | Monday\n");
        writer.close();

        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof Deadline);

        Deadline deadline = (Deadline) tasks.get(0);
        assertFalse(deadline.hasDateTime());
        assertEquals("Monday", deadline.getByAsString());
    }

    @Test
    public void load_validEventWithDateTime_loadsEventCorrectly() throws IOException {
        FileWriter writer = new FileWriter(testFile);
        writer.write("E | 0 | NORMAL | meeting | 2024-12-15T10:00:00 | 2024-12-15T12:00:00\n");
        writer.close();

        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof Event);

        Event event = (Event) tasks.get(0);
        assertTrue(event.hasDateTime());
        assertEquals(LocalDateTime.of(2024, 12, 15, 10, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2024, 12, 15, 12, 0), event.getTo());
    }

    @Test
    public void load_validEventWithString_loadsEventCorrectly() throws IOException {
        FileWriter writer = new FileWriter(testFile);
        writer.write("E | 0 | NORMAL | meeting | Monday 10am to Monday 12pm\n");
        writer.close();

        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof Event);

        Event event = (Event) tasks.get(0);
        assertFalse(event.hasDateTime());
        assertEquals("Monday 10am to Monday 12pm", event.getDuration());
    }

    @Test
    public void load_corruptedLine_skipsCorruptedData() throws IOException {
        FileWriter writer = new FileWriter(testFile);
        writer.write("T | 0 | NORMAL | valid todo\n");
        writer.write("CORRUPTED LINE\n");
        writer.write("D | 0 | NORMAL | valid deadline | Monday\n");
        writer.close();

        ArrayList<Task> tasks = storage.load();
        assertEquals(2, tasks.size()); // Should skip corrupted line
        assertTrue(tasks.get(0) instanceof Todo);
        assertTrue(tasks.get(1) instanceof Deadline);
    }

    @Test
    public void load_emptyLines_skipsEmptyLines() throws IOException {
        FileWriter writer = new FileWriter(testFile);
        writer.write("T | 0 | NORMAL | todo task\n");
        writer.write("\n");
        writer.write("   \n");
        writer.write("D | 0 | NORMAL | deadline task | Monday\n");
        writer.close();

        ArrayList<Task> tasks = storage.load();
        assertEquals(2, tasks.size());
    }

    @Test
    public void load_invalidTaskType_skipsInvalidTask() throws IOException {
        FileWriter writer = new FileWriter(testFile);
        writer.write("T | 0 | NORMAL | valid todo\n");
        writer.write("X | 0 | NORMAL | invalid task type | data\n");
        writer.write("D | 0 | NORMAL | valid deadline | Monday\n");
        writer.close();

        ArrayList<Task> tasks = storage.load();
        assertEquals(2, tasks.size());
    }

    @Test
    public void load_invalidStatus_skipsInvalidTask() throws IOException {
        FileWriter writer = new FileWriter(testFile);
        writer.write("T | 0 | NORMAL | valid todo\n");
        writer.write("T | abc | NORMAL | invalid status | \n");
        writer.write("D | 0 | NORMAL | valid deadline | Monday\n");
        writer.close();

        ArrayList<Task> tasks = storage.load();
        assertEquals(2, tasks.size());
    }

    // Round-trip tests (save then load)
    @Test
    public void roundTrip_mixedTasks_maintainsAllData() throws IOException {
        ArrayList<Task> originalTasks = new ArrayList<>();

        Todo todo = new Todo("todo task");
        todo.setDone(true);
        originalTasks.add(todo);

        Deadline deadline = new Deadline("deadline task", LocalDateTime.of(2024, 12, 15, 14, 30));
        originalTasks.add(deadline);

        Event event = new Event("event task", LocalDateTime.of(2024, 12, 15, 10, 0),
                LocalDateTime.of(2024, 12, 15, 12, 0));
        originalTasks.add(event);

        // Save and load
        storage.save(originalTasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(originalTasks.size(), loadedTasks.size());

        // Check todo
        assertTrue(loadedTasks.get(0) instanceof Todo);
        assertEquals("todo task", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(0).isDone());

        // Check deadline
        assertTrue(loadedTasks.get(1) instanceof Deadline);
        Deadline loadedDeadline = (Deadline) loadedTasks.get(1);
        assertTrue(loadedDeadline.hasDateTime());
        assertEquals(LocalDateTime.of(2024, 12, 15, 14, 30), loadedDeadline.getBy());

        // Check event
        assertTrue(loadedTasks.get(2) instanceof Event);
        Event loadedEvent = (Event) loadedTasks.get(2);
        assertTrue(loadedEvent.hasDateTime());
        assertEquals(LocalDateTime.of(2024, 12, 15, 10, 0), loadedEvent.getFrom());
        assertEquals(LocalDateTime.of(2024, 12, 15, 12, 0), loadedEvent.getTo());
    }
}
