package luffy.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import luffy.exception.LuffyException;
import luffy.task.Task;
import luffy.task.Todo;
import luffy.task.Deadline;
import luffy.task.Event;
import luffy.task.TaskList;
import luffy.ui.Ui;
import luffy.storage.Storage;

public class CommandTest {
    // Mock classes for testing
    private static class MockTaskList extends TaskList {
        private ArrayList<Task> tasks = new ArrayList<>();
        private boolean addCalled = false;
        private boolean removeCalled = false;
        private int removeIndex = -1;
        private boolean getTasksOnDateCalled = false;
        private LocalDateTime requestedDate;
        private ArrayList<Task> tasksOnDateResult = new ArrayList<>();

        @Override
        public void add(Task task) {
            addCalled = true;
            tasks.add(task);
        }

        @Override
        public Task get(int index) {
            return tasks.get(index);
        }

        @Override
        public Task remove(int index) {
            removeCalled = true;
            removeIndex = index;
            return tasks.remove(index);
        }

        @Override
        public int size() {
            return tasks.size();
        }

        @Override
        public ArrayList<Task> getTasks() {
            return tasks;
        }

        @Override
        public String getTaskCountMessage() {
            return "Now you have " + tasks.size() + " tasks in the list.";
        }

        @Override
        public ArrayList<Task> getTasksOnDate(LocalDateTime targetDate) {
            getTasksOnDateCalled = true;
            requestedDate = targetDate;
            return tasksOnDateResult;
        }

        // Helper methods for testing
        public boolean wasAddCalled() {
            return addCalled;
        }

        public boolean wasRemoveCalled() {
            return removeCalled;
        }

        public int getRemovedIndex() {
            return removeIndex;
        }

        public boolean wasGetTasksOnDateCalled() {
            return getTasksOnDateCalled;
        }

        public LocalDateTime getRequestedDate() {
            return requestedDate;
        }

        public void setTasksOnDateResult(ArrayList<Task> result) {
            this.tasksOnDateResult = result;
        }

        public void addTask(Task task) {
            tasks.add(task);
        } // Direct add for setup
    }

    private static class MockUi extends Ui {
        private boolean showTasksOnDateCalled = false;
        private ArrayList<Task> shownTasks;
        private LocalDateTime shownDate;
        private boolean showWelcomeCalled = false;
        private boolean showGoodbyeCalled = false;

        @Override
        public void showTasksOnDate(ArrayList<Task> matchingTasks, LocalDateTime targetDate) {
            showTasksOnDateCalled = true;
            shownTasks = matchingTasks;
            shownDate = targetDate;
        }

        @Override
        public void showWelcome() {
            showWelcomeCalled = true;
        }

        @Override
        public void showGoodbye() {
            showGoodbyeCalled = true;
        }

        // Helper methods for testing
        public boolean wasShowTasksOnDateCalled() {
            return showTasksOnDateCalled;
        }

        public ArrayList<Task> getShownTasks() {
            return shownTasks;
        }

        public LocalDateTime getShownDate() {
            return shownDate;
        }

        public boolean wasShowWelcomeCalled() {
            return showWelcomeCalled;
        }

        public boolean wasShowGoodbyeCalled() {
            return showGoodbyeCalled;
        }
    }

    private static class MockStorage extends Storage {
        private boolean saveCalled = false;
        private ArrayList<Task> savedTasks;
        private boolean shouldThrowIOException = false;

        public MockStorage() {
            super("mock_file.txt");
        }

        @Override
        public void save(ArrayList<Task> tasks) throws IOException {
            if (shouldThrowIOException) {
                throw new IOException("Mock IO Exception");
            }
            saveCalled = true;
            savedTasks = new ArrayList<>(tasks);
        }

        // Helper methods for testing
        public boolean wasSaveCalled() {
            return saveCalled;
        }

        public ArrayList<Task> getSavedTasks() {
            return savedTasks;
        }

        public void setShouldThrowIOException(boolean shouldThrow) {
            this.shouldThrowIOException = shouldThrow;
        }
    }

    private MockTaskList mockTasks;
    private MockUi mockUi;
    private MockStorage mockStorage;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        mockTasks = new MockTaskList();
        mockUi = new MockUi();
        mockStorage = new MockStorage();

        // Capture System.out for commands that print directly
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        // Restore System.out
        System.setOut(originalOut);
    }

    // Tests for AddTodoCommand
    @Test
    public void addTodoCommand_execute_addsTaskAndSaves() throws Exception {
        AddTodoCommand command = new AddTodoCommand("read book");

        command.execute(mockTasks, mockUi, mockStorage);

        assertTrue(mockTasks.wasAddCalled());
        assertTrue(mockStorage.wasSaveCalled());
        assertEquals(1, mockTasks.size());
        assertTrue(mockTasks.get(0) instanceof Todo);
        assertEquals("read book", mockTasks.get(0).getDescription());

        String output = outputStream.toString();
        assertTrue(output.contains("HAI! TASK ADDED:"));
        assertTrue(output.contains("read book"));
    }

    @Test
    public void addTodoCommand_executeWithIOException_throwsIOException() {
        mockStorage.setShouldThrowIOException(true);
        AddTodoCommand command = new AddTodoCommand("read book");

        assertThrows(IOException.class, () -> {
            command.execute(mockTasks, mockUi, mockStorage);
        });
    }

    // Tests for AddDeadlineCommand
    @Test
    public void addDeadlineCommand_execute_addsTaskAndSaves() throws Exception {
        AddDeadlineCommand command = new AddDeadlineCommand("return book", "2024-12-15 14:30");

        command.execute(mockTasks, mockUi, mockStorage);

        assertTrue(mockTasks.wasAddCalled());
        assertTrue(mockStorage.wasSaveCalled());
        assertEquals(1, mockTasks.size());
        assertTrue(mockTasks.get(0) instanceof Deadline);
        assertEquals("return book", mockTasks.get(0).getDescription());

        String output = outputStream.toString();
        assertTrue(output.contains("HAI! TASK ADDED:"));
    }

    // Tests for AddEventCommand
    @Test
    public void addEventCommand_execute_addsTaskAndSaves() throws Exception {
        AddEventCommand command =
                new AddEventCommand("meeting", "2024-12-15 10:00", "2024-12-15 12:00");

        command.execute(mockTasks, mockUi, mockStorage);

        assertTrue(mockTasks.wasAddCalled());
        assertTrue(mockStorage.wasSaveCalled());
        assertEquals(1, mockTasks.size());
        assertTrue(mockTasks.get(0) instanceof Event);
        assertEquals("meeting", mockTasks.get(0).getDescription());

        String output = outputStream.toString();
        assertTrue(output.contains("HAI! TASK ADDED:"));
    }

    // Tests for MarkCommand
    @Test
    public void markCommand_validTaskNumber_marksTaskAndSaves() throws Exception {
        // Setup: add a task to mock list
        Todo todo = new Todo("test task");
        mockTasks.addTask(todo);

        MarkCommand command = new MarkCommand(1);
        command.execute(mockTasks, mockUi, mockStorage);

        assertTrue(mockStorage.wasSaveCalled());
        assertTrue(todo.isDone());

        String output = outputStream.toString();
        assertTrue(output.contains("KAIZOKU!"));
    }

    @Test
    public void markCommand_invalidTaskNumber_throwsException() {
        MarkCommand command = new MarkCommand(5); // No tasks in list

        LuffyException exception = assertThrows(LuffyException.class, () -> {
            command.execute(mockTasks, mockUi, mockStorage);
        });

        assertTrue(exception.getMessage().contains("That doesn't exist"));
    }

    @Test
    public void markCommand_taskNumberTooLow_throwsException() {
        mockTasks.addTask(new Todo("test task"));
        MarkCommand command = new MarkCommand(0);

        assertThrows(LuffyException.class, () -> {
            command.execute(mockTasks, mockUi, mockStorage);
        });
    }

    // Tests for UnmarkCommand
    @Test
    public void unmarkCommand_validTaskNumber_unmarksTaskAndSaves() throws Exception {
        // Setup: add a completed task
        Todo todo = new Todo("test task");
        todo.setDone(true);
        mockTasks.addTask(todo);

        UnmarkCommand command = new UnmarkCommand(1);
        command.execute(mockTasks, mockUi, mockStorage);

        assertTrue(mockStorage.wasSaveCalled());
        assertFalse(todo.isDone());

        String output = outputStream.toString();
        assertTrue(output.contains("NANI?"));
    }

    @Test
    public void unmarkCommand_invalidTaskNumber_throwsException() {
        UnmarkCommand command = new UnmarkCommand(5);

        assertThrows(LuffyException.class, () -> {
            command.execute(mockTasks, mockUi, mockStorage);
        });
    }

    // Tests for DeleteCommand
    @Test
    public void deleteCommand_validTaskNumber_deletesTaskAndSaves() throws Exception {
        Todo todo = new Todo("test task");
        mockTasks.addTask(todo);

        DeleteCommand command = new DeleteCommand(1);
        command.execute(mockTasks, mockUi, mockStorage);

        assertTrue(mockTasks.wasRemoveCalled());
        assertEquals(0, mockTasks.getRemovedIndex());
        assertTrue(mockStorage.wasSaveCalled());
        assertEquals(0, mockTasks.size());

        String output = outputStream.toString();
        assertTrue(output.contains("HAI! TASK DELETED:"));
    }

    @Test
    public void deleteCommand_invalidTaskNumber_throwsException() {
        DeleteCommand command = new DeleteCommand(5);

        LuffyException exception = assertThrows(LuffyException.class, () -> {
            command.execute(mockTasks, mockUi, mockStorage);
        });

        assertTrue(exception.getMessage().contains("That doesn't exist"));
    }

    // Tests for ListCommand
    @Test
    public void listCommand_execute_showsTaskList() throws Exception {
        ListCommand command = new ListCommand();
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the tasks in your list:"));
    }

    @Test
    public void listCommand_emptyList_showsHeaderOnly() throws Exception {
        ListCommand command = new ListCommand();
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the tasks in your list:"));
        // For empty list, only header is shown, no task numbers
        assertFalse(output.contains("1."));
    }

    @Test
    public void listCommand_withTasks_showsAllTasks() throws Exception {
        mockTasks.addTask(new Todo("task 1"));
        mockTasks.addTask(new Todo("task 2"));

        ListCommand command = new ListCommand();
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("1."));
        assertTrue(output.contains("2."));
        assertTrue(output.contains("task 1"));
        assertTrue(output.contains("task 2"));
    }

    // Tests for DueCommand
    @Test
    public void dueCommand_validDate_callsUiShowTasksOnDate() throws Exception {
        LocalDateTime targetDate = LocalDateTime.of(2024, 12, 15, 0, 0);
        ArrayList<Task> matchingTasks = new ArrayList<>();
        matchingTasks.add(new Todo("matching task"));
        mockTasks.setTasksOnDateResult(matchingTasks);

        DueCommand command = new DueCommand("2024-12-15");
        command.execute(mockTasks, mockUi, mockStorage);

        assertTrue(mockTasks.wasGetTasksOnDateCalled());
        assertTrue(mockUi.wasShowTasksOnDateCalled());
        assertEquals(matchingTasks, mockUi.getShownTasks());
        // Note: exact date comparison may vary due to parsing, so we check the date part
        assertEquals(2024, mockUi.getShownDate().getYear());
        assertEquals(12, mockUi.getShownDate().getMonthValue());
        assertEquals(15, mockUi.getShownDate().getDayOfMonth());
    }

    @Test
    public void dueCommand_invalidDate_throwsException() {
        DueCommand command = new DueCommand("invalid-date");

        assertThrows(LuffyException.class, () -> {
            command.execute(mockTasks, mockUi, mockStorage);
        });
    }

    // Tests for ExitCommand
    @Test
    public void exitCommand_execute_callsUiShowGoodbye() throws Exception {
        ExitCommand command = new ExitCommand();
        command.execute(mockTasks, mockUi, mockStorage);

        assertTrue(mockUi.wasShowGoodbyeCalled());
    }

    @Test
    public void exitCommand_isExit_returnsTrue() {
        ExitCommand command = new ExitCommand();
        assertTrue(command.isExit());
    }

    // Tests for base Command class behavior
    @Test
    public void command_isExit_defaultsToFalse() {
        AddTodoCommand command = new AddTodoCommand("test");
        assertFalse(command.isExit());
    }
}
