package luffy.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TaskListTest {
    private TaskList taskList;
    private LocalDateTime testDate1;
    private LocalDateTime testDate2;
    private LocalDateTime testDate3;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
        testDate1 = LocalDateTime.of(2024, 12, 15, 10, 0); // Dec 15, 2024 10:00 AM
        testDate2 = LocalDateTime.of(2024, 12, 16, 14, 30); // Dec 16, 2024 2:30 PM
        testDate3 = LocalDateTime.of(2024, 12, 17, 23, 59); // Dec 17, 2024 11:59 PM
    }

    @Test
    public void getTasksOnDate_emptyTaskList_returnsEmptyList() {
        ArrayList<Task> result = taskList.getTasksOnDate(testDate1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getTasksOnDate_noMatchingTasks_returnsEmptyList() {
        // Add tasks with different dates
        Deadline deadline = new Deadline("deadline task", testDate1);
        Event event = new Event("event task", testDate2, testDate2.plusHours(2));
        taskList.add(deadline);
        taskList.add(event);

        // Search for a different date
        LocalDateTime differentDate = LocalDateTime.of(2024, 12, 20, 10, 0);
        ArrayList<Task> result = taskList.getTasksOnDate(differentDate);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getTasksOnDate_matchingDeadline_returnsDeadline() {
        Deadline deadline = new Deadline("deadline task", testDate1);
        taskList.add(deadline);

        ArrayList<Task> result = taskList.getTasksOnDate(testDate1);
        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0));
    }

    @Test
    public void getTasksOnDate_matchingEvent_returnsEvent() {
        Event event = new Event("event task", testDate1, testDate1.plusHours(3));
        taskList.add(event);

        ArrayList<Task> result = taskList.getTasksOnDate(testDate1);
        assertEquals(1, result.size());
        assertEquals(event, result.get(0));
    }

    @Test
    public void getTasksOnDate_eventSpansMultipleDays_returnsEventForAllDays() {
        // Create event spanning 3 days
        LocalDateTime eventStart = LocalDateTime.of(2024, 12, 15, 10, 0);
        LocalDateTime eventEnd = LocalDateTime.of(2024, 12, 17, 18, 0);
        Event event = new Event("multi-day event", eventStart, eventEnd);
        taskList.add(event);

        // Test each day in the range
        ArrayList<Task> resultDay1 = taskList.getTasksOnDate(LocalDateTime.of(2024, 12, 15, 5, 0));
        ArrayList<Task> resultDay2 = taskList.getTasksOnDate(LocalDateTime.of(2024, 12, 16, 12, 0));
        ArrayList<Task> resultDay3 = taskList.getTasksOnDate(LocalDateTime.of(2024, 12, 17, 20, 0));

        assertEquals(1, resultDay1.size());
        assertEquals(1, resultDay2.size());
        assertEquals(1, resultDay3.size());
        assertEquals(event, resultDay1.get(0));
        assertEquals(event, resultDay2.get(0));
        assertEquals(event, resultDay3.get(0));
    }

    @Test
    public void getTasksOnDate_multipleMatchingTasks_returnsAllMatching() {
        Deadline deadline = new Deadline("deadline task", testDate1);
        Event event = new Event("event task", testDate1, testDate1.plusHours(2));
        Todo todo = new Todo("todo task"); // Should not match

        taskList.add(deadline);
        taskList.add(event);
        taskList.add(todo);

        ArrayList<Task> result = taskList.getTasksOnDate(testDate1);
        assertEquals(2, result.size());
        assertTrue(result.contains(deadline));
        assertTrue(result.contains(event));
        assertFalse(result.contains(todo));
    }

    @Test
    public void getTasksOnDate_ignoreTimeComponent_matchesSameDate() {
        // Create deadline at specific time
        LocalDateTime deadlineTime = LocalDateTime.of(2024, 12, 15, 10, 0);
        Deadline deadline = new Deadline("deadline task", deadlineTime);
        taskList.add(deadline);

        // Search with different time but same date
        LocalDateTime searchTime = LocalDateTime.of(2024, 12, 15, 23, 59);
        ArrayList<Task> result = taskList.getTasksOnDate(searchTime);
        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0));
    }

    @Test
    public void getTasksOnDate_oldStringBasedTasks_ignoresOldTasks() {
        // Create deadline with string (old format) - should not match
        Deadline oldDeadline = new Deadline("old deadline", "some old date string");
        Event oldEvent = new Event("old event", "old start", "old end");
        taskList.add(oldDeadline);
        taskList.add(oldEvent);

        ArrayList<Task> result = taskList.getTasksOnDate(testDate1);
        assertTrue(result.isEmpty());
    }
}
