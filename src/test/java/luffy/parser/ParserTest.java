package luffy.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import luffy.exception.LuffyException;
import luffy.command.*;

public class ParserTest {

    // Tests for parseDateTime method
    @Test
    public void parseDateTime_validYearMonthDay_returnsCorrectDateTime() throws LuffyException {
        LocalDateTime result = Parser.parseDateTime("2024-12-15");
        LocalDateTime expected = LocalDateTime.of(2024, 12, 15, 23, 59);
        assertEquals(expected, result);
    }

    @Test
    public void parseDateTime_validYearMonthDayWithTime24Hour_returnsCorrectDateTime()
            throws LuffyException {
        LocalDateTime result = Parser.parseDateTime("2024-12-15 1430");
        LocalDateTime expected = LocalDateTime.of(2024, 12, 15, 14, 30);
        assertEquals(expected, result);
    }

    @Test
    public void parseDateTime_validYearMonthDayWithTimeColon_returnsCorrectDateTime()
            throws LuffyException {
        LocalDateTime result = Parser.parseDateTime("2024-12-15 14:30");
        LocalDateTime expected = LocalDateTime.of(2024, 12, 15, 14, 30);
        assertEquals(expected, result);
    }

    @Test
    public void parseDateTime_validYearMonthDayWithTime12Hour_returnsCorrectDateTime()
            throws LuffyException {
        LocalDateTime result = Parser.parseDateTime("2024-12-15 2:30 pm");
        LocalDateTime expected = LocalDateTime.of(2024, 12, 15, 14, 30);
        assertEquals(expected, result);
    }

    @Test
    public void parseDateTime_validDayMonthYear_returnsCorrectDateTime() throws LuffyException {
        LocalDateTime result = Parser.parseDateTime("15/12/2024");
        LocalDateTime expected = LocalDateTime.of(2024, 12, 15, 23, 59);
        assertEquals(expected, result);
    }

    @Test
    public void parseDateTime_validDayMonthYearWithTime_returnsCorrectDateTime()
            throws LuffyException {
        LocalDateTime result = Parser.parseDateTime("15/12/2024 1430");
        LocalDateTime expected = LocalDateTime.of(2024, 12, 15, 14, 30);
        assertEquals(expected, result);
    }

    @Test
    public void parseDateTime_validDayMonthYearWith12Hour_returnsCorrectDateTime()
            throws LuffyException {
        LocalDateTime result = Parser.parseDateTime("2/12/2024 6:00 pm");
        LocalDateTime expected = LocalDateTime.of(2024, 12, 2, 18, 0);
        assertEquals(expected, result);
    }

    @Test
    public void parseDateTime_invalidFormat_throwsLuffyException() {
        assertThrows(LuffyException.class, () -> {
            Parser.parseDateTime("invalid-date");
        });
    }

    @Test
    public void parseDateTime_emptyString_throwsLuffyException() {
        assertThrows(LuffyException.class, () -> {
            Parser.parseDateTime("");
        });
    }

    @Test
    public void parseDateTime_whitespaceOnly_throwsLuffyException() {
        assertThrows(LuffyException.class, () -> {
            Parser.parseDateTime("   ");
        });
    }

    // Tests for validateEventTimes method
    @Test
    public void validateEventTimes_validTimes_doesNotThrowException() {
        LocalDateTime from = LocalDateTime.of(2024, 12, 15, 10, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 15, 12, 0);

        assertDoesNotThrow(() -> {
            Parser.validateEventTimes(from, to, "10:00", "12:00");
        });
    }

    @Test
    public void validateEventTimes_startAfterEnd_throwsLuffyException() {
        LocalDateTime from = LocalDateTime.of(2024, 12, 15, 12, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 15, 10, 0);

        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateEventTimes(from, to, "12:00", "10:00");
        });
        assertTrue(exception.getMessage().contains("cannot be after"));
    }

    @Test
    public void validateEventTimes_sameStartAndEnd_throwsLuffyException() {
        LocalDateTime from = LocalDateTime.of(2024, 12, 15, 10, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 15, 10, 0);

        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateEventTimes(from, to, "10:00", "10:00");
        });
        assertTrue(exception.getMessage().contains("cannot be the same"));
    }

    // Tests for validateTodoCommand method
    @Test
    public void validateTodoCommand_validInput_doesNotThrowException() {
        assertDoesNotThrow(() -> {
            Parser.validateTodoCommand("todo read book");
        });
    }

    @Test
    public void validateTodoCommand_emptyDescription_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateTodoCommand("todo");
        });
        assertTrue(exception.getMessage().contains("needs a description"));
    }

    @Test
    public void validateTodoCommand_onlyWhitespaceDescription_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateTodoCommand("todo   ");
        });
        assertTrue(exception.getMessage().contains("needs a description"));
    }

    // Tests for validateDeadlineCommand method
    @Test
    public void validateDeadlineCommand_validInput_doesNotThrowException() {
        assertDoesNotThrow(() -> {
            Parser.validateDeadlineCommand("deadline return book /by Monday");
        });
    }

    @Test
    public void validateDeadlineCommand_emptyDescription_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateDeadlineCommand("deadline");
        });
        assertTrue(exception.getMessage().contains("tell me what the deadline is for"));
    }

    @Test
    public void validateDeadlineCommand_missingByKeyword_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateDeadlineCommand("deadline return book Monday");
        });
        assertTrue(exception.getMessage().contains("/by"));
    }

    @Test
    public void validateDeadlineCommand_emptyByDate_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateDeadlineCommand("deadline return book /by");
        });
        assertTrue(exception.getMessage().contains("WHEN the deadline is"));
    }

    // Tests for validateEventCommand method
    @Test
    public void validateEventCommand_validInput_doesNotThrowException() {
        assertDoesNotThrow(() -> {
            Parser.validateEventCommand("event meeting /from Monday /to Tuesday");
        });
    }

    @Test
    public void validateEventCommand_emptyDescription_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateEventCommand("event");
        });
        assertTrue(exception.getMessage().contains("Give me a description"));
    }

    @Test
    public void validateEventCommand_missingFromKeyword_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateEventCommand("event meeting Monday /to Tuesday");
        });
        assertTrue(exception.getMessage().contains("Missing '/from'"));
    }

    @Test
    public void validateEventCommand_missingToKeyword_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateEventCommand("event meeting /from Monday Tuesday");
        });
        assertTrue(exception.getMessage().contains("Missing '/to'"));
    }

    @Test
    public void validateEventCommand_wrongOrder_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateEventCommand("event meeting /to Tuesday /from Monday");
        });
        assertTrue(exception.getMessage().contains("'/from' should come before '/to'"));
    }

    @Test
    public void validateEventCommand_emptyFromDate_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateEventCommand("event meeting /from /to Tuesday");
        });
        assertTrue(exception.getMessage().contains("when it starts"));
    }

    @Test
    public void validateEventCommand_emptyToDate_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateEventCommand("event meeting /from Monday /to");
        });
        assertTrue(exception.getMessage().contains("when it ends"));
    }

    // Tests for validateMarkUnmarkCommand method
    @Test
    public void validateMarkUnmarkCommand_validMarkCommand_doesNotThrowException() {
        assertDoesNotThrow(() -> {
            Parser.validateMarkUnmarkCommand("mark 1", true, 5);
        });
    }

    @Test
    public void validateMarkUnmarkCommand_validUnmarkCommand_doesNotThrowException() {
        assertDoesNotThrow(() -> {
            Parser.validateMarkUnmarkCommand("unmark 3", false, 5);
        });
    }

    @Test
    public void validateMarkUnmarkCommand_missingTaskNumber_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateMarkUnmarkCommand("mark", true, 5);
        });
        assertTrue(exception.getMessage().contains("Give me a number"));
    }

    @Test
    public void validateMarkUnmarkCommand_invalidTaskNumber_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateMarkUnmarkCommand("mark abc", true, 5);
        });
        assertTrue(exception.getMessage().contains("not a number"));
    }

    @Test
    public void validateMarkUnmarkCommand_taskNumberTooLow_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateMarkUnmarkCommand("mark 0", true, 5);
        });
        assertTrue(exception.getMessage().contains("doesn't exist"));
    }

    @Test
    public void validateMarkUnmarkCommand_taskNumberTooHigh_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateMarkUnmarkCommand("mark 10", true, 5);
        });
        assertTrue(exception.getMessage().contains("doesn't exist"));
    }

    // Tests for validateDeleteCommand method
    @Test
    public void validateDeleteCommand_validInput_doesNotThrowException() {
        assertDoesNotThrow(() -> {
            Parser.validateDeleteCommand("delete 1", 5);
        });
    }

    @Test
    public void validateDeleteCommand_missingTaskNumber_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateDeleteCommand("delete", 5);
        });
        assertTrue(exception.getMessage().contains("Give me a number"));
    }

    @Test
    public void validateDeleteCommand_invalidTaskNumber_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateDeleteCommand("delete xyz", 5);
        });
        assertTrue(exception.getMessage().contains("not a number"));
    }

    @Test
    public void validateDeleteCommand_taskNumberOutOfRange_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.validateDeleteCommand("delete 10", 5);
        });
        assertTrue(exception.getMessage().contains("doesn't exist"));
    }

    // Tests for parse method (Command parsing)
    @Test
    public void parse_byeCommand_returnsExitCommand() throws LuffyException {
        Command result = Parser.parse("bye");
        assertTrue(result instanceof ExitCommand);
        assertTrue(result.isExit());
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws LuffyException {
        Command result = Parser.parse("list");
        assertTrue(result instanceof ListCommand);
        assertFalse(result.isExit());
    }

    @Test
    public void parse_todoCommand_returnsAddTodoCommand() throws LuffyException {
        Command result = Parser.parse("todo read book");
        assertTrue(result instanceof AddTodoCommand);
        assertFalse(result.isExit());
    }

    @Test
    public void parse_deadlineCommand_returnsAddDeadlineCommand() throws LuffyException {
        Command result = Parser.parse("deadline return book /by Monday");
        assertTrue(result instanceof AddDeadlineCommand);
        assertFalse(result.isExit());
    }

    @Test
    public void parse_eventCommand_returnsAddEventCommand() throws LuffyException {
        Command result = Parser.parse("event meeting /from Monday /to Tuesday");
        assertTrue(result instanceof AddEventCommand);
        assertFalse(result.isExit());
    }

    @Test
    public void parse_markCommand_returnsMarkCommand() throws LuffyException {
        Command result = Parser.parse("mark 1");
        assertTrue(result instanceof MarkCommand);
        assertFalse(result.isExit());
    }

    @Test
    public void parse_unmarkCommand_returnsUnmarkCommand() throws LuffyException {
        Command result = Parser.parse("unmark 2");
        assertTrue(result instanceof UnmarkCommand);
        assertFalse(result.isExit());
    }

    @Test
    public void parse_deleteCommand_returnsDeleteCommand() throws LuffyException {
        Command result = Parser.parse("delete 3");
        assertTrue(result instanceof DeleteCommand);
        assertFalse(result.isExit());
    }

    @Test
    public void parse_dueCommand_returnsDueCommand() throws LuffyException {
        Command result = Parser.parse("due 2024-12-15");
        assertTrue(result instanceof DueCommand);
        assertFalse(result.isExit());
    }

    @Test
    public void parse_caseInsensitiveCommands_worksCorrectly() throws LuffyException {
        assertTrue(Parser.parse("BYE") instanceof ExitCommand);
        assertTrue(Parser.parse("List") instanceof ListCommand);
        assertTrue(Parser.parse("TODO read") instanceof AddTodoCommand);
        assertTrue(Parser.parse("DEADLINE task /by Monday") instanceof AddDeadlineCommand);
        assertTrue(Parser.parse("Event party /from Mon /to Tue") instanceof AddEventCommand);
        assertTrue(Parser.parse("MARK 1") instanceof MarkCommand);
        assertTrue(Parser.parse("Unmark 1") instanceof UnmarkCommand);
        assertTrue(Parser.parse("DELETE 1") instanceof DeleteCommand);
        assertTrue(Parser.parse("Due 2024-12-15") instanceof DueCommand);
    }

    @Test
    public void parse_unknownCommand_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.parse("unknown command");
        });
        assertTrue(exception.getMessage().contains("I don't understand"));
    }

    @Test
    public void parse_emptyCommand_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.parse("");
        });
        assertTrue(exception.getMessage().contains("Please enter a command"));
    }

    @Test
    public void parse_whitespaceOnlyCommand_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.parse("   ");
        });
        assertTrue(exception.getMessage().contains("Please enter a command"));
    }

    @Test
    public void parse_invalidTodoCommand_throwsLuffyException() {
        assertThrows(LuffyException.class, () -> {
            Parser.parse("todo");
        });
    }

    @Test
    public void parse_invalidDeadlineCommand_throwsLuffyException() {
        assertThrows(LuffyException.class, () -> {
            Parser.parse("deadline");
        });
    }

    @Test
    public void parse_invalidEventCommand_throwsLuffyException() {
        assertThrows(LuffyException.class, () -> {
            Parser.parse("event");
        });
    }

    @Test
    public void parse_invalidMarkCommand_throwsLuffyException() {
        assertThrows(LuffyException.class, () -> {
            Parser.parse("mark abc");
        });
    }

    @Test
    public void parse_invalidDeleteCommand_throwsLuffyException() {
        assertThrows(LuffyException.class, () -> {
            Parser.parse("delete xyz");
        });
    }

    @Test
    public void parse_invalidDueCommand_throwsLuffyException() {
        assertThrows(LuffyException.class, () -> {
            Parser.parse("due");
        });
    }

    // Tests for find command parsing
    @Test
    public void parse_findCommand_returnsFindCommand() throws LuffyException {
        Command result = Parser.parse("find book");
        assertTrue(result instanceof FindCommand);
        assertFalse(result.isExit());
    }

    @Test
    public void parse_findCommandMultipleKeywords_returnsFindCommand() throws LuffyException {
        Command result = Parser.parse("find read book");
        assertTrue(result instanceof FindCommand);
    }

    @Test
    public void parse_findCommandCaseInsensitive_returnsFindCommand() throws LuffyException {
        Command result1 = Parser.parse("find book");
        Command result2 = Parser.parse("Find book");
        Command result3 = Parser.parse("FIND book");

        assertTrue(result1 instanceof FindCommand);
        assertTrue(result2 instanceof FindCommand);
        assertTrue(result3 instanceof FindCommand);
    }

    @Test
    public void parse_findCommandNoKeywords_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.parse("find");
        });
        assertTrue(exception.getMessage().contains("What do you want to find"));
    }

    @Test
    public void parse_findCommandEmptyKeywords_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.parse("find   ");
        });
        assertTrue(exception.getMessage().contains("What do you want to find"));
    }

    @Test
    public void parse_findCommandWhitespaceKeywords_throwsLuffyException() {
        LuffyException exception = assertThrows(LuffyException.class, () -> {
            Parser.parse("find  ");
        });
        assertTrue(exception.getMessage().contains("What do you want to find"));
    }
}
