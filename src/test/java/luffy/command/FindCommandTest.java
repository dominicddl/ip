package luffy.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import luffy.task.Task;
import luffy.task.Todo;
import luffy.task.Deadline;
import luffy.task.Event;
import luffy.task.TaskList;
import luffy.ui.Ui;
import luffy.storage.Storage;

public class FindCommandTest {
    // Mock classes for testing
    private static class MockTaskList extends TaskList {
        private ArrayList<Task> tasks = new ArrayList<>();

        @Override
        public Task get(int index) {
            return tasks.get(index);
        }

        @Override
        public int size() {
            return tasks.size();
        }

        // Helper method for setup
        public void addTask(Task task) {
            tasks.add(task);
        }
    }

    private static class MockUi extends Ui {
        // Mock UI - we'll capture output via System.out instead
    }

    private static class MockStorage extends Storage {
        public MockStorage() {
            super("mock_file.txt");
        }
        // Mock storage - not used by FindCommand
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

        // Capture System.out for output verification
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        // Restore System.out
        System.setOut(originalOut);
    }

    // Tests for single keyword search
    @Test
    public void findCommand_singleKeywordMatch_showsMatchingTasks() throws Exception {
        // Setup tasks
        mockTasks.addTask(new Todo("read book"));
        mockTasks.addTask(new Todo("buy groceries"));
        mockTasks.addTask(new Todo("return book"));

        FindCommand command = new FindCommand(new String[] {"book"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[T][ ] return book"));
        assertFalse(output.contains("buy groceries"));
    }

    @Test
    public void findCommand_singleKeywordNoMatch_showsNoMatchMessage() throws Exception {
        // Setup tasks
        mockTasks.addTask(new Todo("read book"));
        mockTasks.addTask(new Todo("buy groceries"));

        FindCommand command = new FindCommand(new String[] {"xyz"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("No matching tasks found."));
        assertFalse(output.contains("Here are the matching tasks"));
    }

    @Test
    public void findCommand_emptyTaskList_showsNoMatchMessage() throws Exception {
        FindCommand command = new FindCommand(new String[] {"book"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("No matching tasks found."));
    }

    // Tests for multiple keyword search
    @Test
    public void findCommand_multipleKeywordsAllMatch_showsMatchingTasks() throws Exception {
        // Setup tasks
        mockTasks.addTask(new Todo("read programming book"));
        mockTasks.addTask(new Todo("read history book"));
        mockTasks.addTask(new Todo("programming tutorial"));

        FindCommand command = new FindCommand(new String[] {"read", "book"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] read programming book"));
        assertTrue(output.contains("2.[T][ ] read history book"));
        assertFalse(output.contains("programming tutorial"));
    }

    @Test
    public void findCommand_multipleKeywordsPartialMatch_showsNoMatch() throws Exception {
        // Setup tasks
        mockTasks.addTask(new Todo("read book"));
        mockTasks.addTask(new Todo("programming tutorial"));

        FindCommand command = new FindCommand(new String[] {"read", "programming"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("No matching tasks found."));
    }

    // Tests for case insensitive search
    @Test
    public void findCommand_caseInsensitiveSearch_matchesCorrectly() throws Exception {
        // Setup tasks
        mockTasks.addTask(new Todo("Read BOOK"));
        mockTasks.addTask(new Todo("BUY groceries"));

        FindCommand command = new FindCommand(new String[] {"book"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] Read BOOK"));
        assertFalse(output.contains("BUY groceries"));
    }

    @Test
    public void findCommand_caseInsensitiveKeywords_matchesCorrectly() throws Exception {
        // Setup tasks
        mockTasks.addTask(new Todo("read book"));

        FindCommand command = new FindCommand(new String[] {"READ", "BOOK"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] read book"));
    }

    // Tests for substring matching
    @Test
    public void findCommand_substringMatch_matchesCorrectly() throws Exception {
        // Setup tasks
        mockTasks.addTask(new Todo("notebook"));
        mockTasks.addTask(new Todo("textbook"));
        mockTasks.addTask(new Todo("car"));

        FindCommand command = new FindCommand(new String[] {"book"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] notebook"));
        assertTrue(output.contains("2.[T][ ] textbook"));
        assertFalse(output.contains("car"));
    }

    // Tests with different task types
    @Test
    public void findCommand_differentTaskTypes_matchesAll() throws Exception {
        // Setup tasks of different types
        mockTasks.addTask(new Todo("read book"));
        mockTasks.addTask(new Deadline("return book", "Monday"));
        mockTasks.addTask(new Event("book club meeting", "Saturday", "Sunday"));
        mockTasks.addTask(new Todo("buy groceries"));

        FindCommand command = new FindCommand(new String[] {"book"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[D][ ] return book"));
        assertTrue(output.contains("3.[E][ ] book club meeting"));
        assertFalse(output.contains("buy groceries"));
    }

    @Test
    public void findCommand_completedTasks_includesInResults() throws Exception {
        // Setup tasks with different completion status
        Todo completedTodo = new Todo("read book");
        completedTodo.setDone(true);
        mockTasks.addTask(completedTodo);
        mockTasks.addTask(new Todo("return book"));

        FindCommand command = new FindCommand(new String[] {"book"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][X] read book"));
        assertTrue(output.contains("2.[T][ ] return book"));
    }

    // Tests for sequential numbering
    @Test
    public void findCommand_sequentialNumbering_startsFromOne() throws Exception {
        // Setup many tasks to test numbering
        mockTasks.addTask(new Todo("task 1"));
        mockTasks.addTask(new Todo("book 1"));
        mockTasks.addTask(new Todo("task 2"));
        mockTasks.addTask(new Todo("book 2"));
        mockTasks.addTask(new Todo("book 3"));

        FindCommand command = new FindCommand(new String[] {"book"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("1.[T][ ] book 1"));
        assertTrue(output.contains("2.[T][ ] book 2"));
        assertTrue(output.contains("3.[T][ ] book 3"));
        assertFalse(output.contains("4."));
    }

    // Tests for edge cases
    @Test
    public void findCommand_emptyKeyword_handledByParser() throws Exception {
        // This test verifies that empty keywords are filtered out by parser
        // If somehow an empty keyword gets through, it should match everything
        mockTasks.addTask(new Todo("any task"));

        FindCommand command = new FindCommand(new String[] {""});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] any task"));
    }

    @Test
    public void findCommand_specialCharactersInDescription_matchesCorrectly() throws Exception {
        // Setup tasks with special characters
        mockTasks.addTask(new Todo("read C++ book"));
        mockTasks.addTask(new Todo("learn Java"));

        FindCommand command = new FindCommand(new String[] {"C++"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] read C++ book"));
        assertFalse(output.contains("learn Java"));
    }

    @Test
    public void findCommand_numbersInDescription_matchesCorrectly() throws Exception {
        // Setup tasks with numbers
        mockTasks.addTask(new Todo("chapter 1 reading"));
        mockTasks.addTask(new Todo("chapter 2 reading"));
        mockTasks.addTask(new Todo("general reading"));

        FindCommand command = new FindCommand(new String[] {"1"});
        command.execute(mockTasks, mockUi, mockStorage);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] chapter 1 reading"));
        assertFalse(output.contains("chapter 2 reading"));
        assertFalse(output.contains("general reading"));
    }

    // Test for isExit behavior
    @Test
    public void findCommand_isExit_returnsFalse() {
        FindCommand command = new FindCommand(new String[] {"book"});
        assertFalse(command.isExit());
    }
}
