package luffy.parser;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import luffy.exception.LuffyException;
import luffy.command.*;

/**
 * Deals with making sense of the user command and parsing date/time strings. This class provides
 * static methods to parse user input commands into Command objects and to parse date/time strings
 * in various formats into LocalDateTime objects. It also includes validation methods for different
 * command types.
 */
public class Parser {

    // Date format patterns
    private static final String YYYY_MM_DD_HHMM = "yyyy-MM-dd HHmm";
    private static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    private static final String YYYY_MM_DD_H_MM_A = "yyyy-MM-dd h:mm a";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String D_M_YYYY_HHMM = "d/M/yyyy HHmm";
    private static final String D_M_YYYY_HH_MM = "d/M/yyyy HH:mm";
    private static final String D_M_YYYY_H_MM_A = "d/M/yyyy h:mm a";
    private static final String D_M_YYYY = "d/M/yyyy";

    // Date pattern regex
    private static final String DATE_ONLY_DASH_PATTERN = ".*\\d{4}-\\d{2}-\\d{2}$";
    private static final String DATE_ONLY_SLASH_PATTERN = ".*\\d{1,2}/\\d{1,2}/\\d{4}$";

    // Default time for date-only inputs
    private static final int DEFAULT_HOUR = 23;
    private static final int DEFAULT_MINUTE = 59;

    /**
     * Parses a date/time string into LocalDateTime object. Supports multiple date and time formats
     * with flexible input parsing.
     * 
     * Supported formats: - yyyy-mm-dd (date only, time defaults to 23:59) - yyyy-mm-dd HHmm (date
     * with time in 24-hour format) - yyyy-mm-dd HH:mm (date with time in 24-hour format) -
     * yyyy-mm-dd h:mm AM/PM (date with time in 12-hour format) - d/m/yyyy (date only, time defaults
     * to 23:59) - d/m/yyyy HHmm (date with time in 24-hour format) - d/m/yyyy HH:mm (date with time
     * in 24-hour format) - d/m/yyyy h:mm AM/PM (date with time in 12-hour format)
     *
     * @param dateTimeStr the date/time string to parse
     * @return LocalDateTime object representing the parsed date and time
     * @throws LuffyException if the date/time string cannot be parsed
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) throws LuffyException {
        dateTimeStr = dateTimeStr.trim();

        // Define possible formatters
        DateTimeFormatter[] formatters = {
                // yyyy-mm-dd formats
                DateTimeFormatter.ofPattern(YYYY_MM_DD_HHMM),
                DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM),
                DateTimeFormatter.ofPattern(YYYY_MM_DD_H_MM_A),
                DateTimeFormatter.ofPattern(YYYY_MM_DD),
                // d/m/yyyy formats
                DateTimeFormatter.ofPattern(D_M_YYYY_HHMM),
                DateTimeFormatter.ofPattern(D_M_YYYY_HH_MM),
                DateTimeFormatter.ofPattern(D_M_YYYY_H_MM_A),
                DateTimeFormatter.ofPattern(D_M_YYYY)};

        // Try parsing with each formatter
        for (DateTimeFormatter formatter : formatters) {
            try {
                if (dateTimeStr.matches(DATE_ONLY_DASH_PATTERN)
                        || dateTimeStr.matches(DATE_ONLY_SLASH_PATTERN)) {
                    // Date only formats - parse as LocalDate and set time to default
                    LocalDate date = LocalDate.parse(dateTimeStr, formatter);
                    return date.atTime(DEFAULT_HOUR, DEFAULT_MINUTE);
                } else {
                    // Date with time formats
                    return LocalDateTime.parse(dateTimeStr, formatter);
                }
            } catch (DateTimeParseException e) {
                // Continue to next formatter
            }
        }

        // If no formatter worked, provide helpful error message with suggestions
        String suggestion = getDateFormatSuggestion(dateTimeStr);
        throw new LuffyException("Invalid date/time format: '" + dateTimeStr + "'. " + suggestion
                + " Valid formats: 2019-12-02, 2019-12-02 1800, 2019-12-02 18:00, 2019-12-02 6:00 PM, "
                + "2/12/2019, 2/12/2019 1800, 2/12/2019 18:00, or 2/12/2019 6:00 PM");
    }

    /**
     * Validates that the event start time is before end time.
     */
    public static void validateEventTimes(LocalDateTime from, LocalDateTime to,
            String originalFromStr, String originalToStr) throws LuffyException {
        if (from.isAfter(to)) {
            throw new LuffyException("Event start time '" + originalFromStr
                    + "' cannot be after end time '" + originalToStr + "'!");
        }
        if (from.equals(to)) {
            throw new LuffyException("Event start time and end time cannot be the same! '"
                    + originalFromStr + "' = '" + originalToStr + "'");
        }
    }

    /**
     * Provides helpful suggestions for common date format mistakes based on the invalid input.
     *
     * @param invalidDateString the date string that failed to parse
     * @return a helpful suggestion message for correcting the date format
     */
    private static String getDateFormatSuggestion(String invalidDateString) {
        // Check for common date format mistakes and provide specific suggestions
        if (invalidDateString.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
            return "Did you mean to use '/' instead of '-'? Try: "
                    + invalidDateString.replace("-", "/");
        }
        if (invalidDateString.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
            return "For yyyy/mm/dd format, use '-' instead: " + invalidDateString.replace("/", "-");
        }
        if (invalidDateString.matches("\\d{1,2}/\\d{1,2}/\\d{2}")) {
            return "Use 4-digit year: "
                    + invalidDateString.substring(0, invalidDateString.length() - 2) + "20"
                    + invalidDateString.substring(invalidDateString.length() - 2);
        }
        return "Check the date format and try again.";
    }

    /**
     * Validates todo command input.
     */
    public static void validateTodoCommand(String input) throws LuffyException {
        if (input.trim().length() <= 4 || input.substring(4).trim().isEmpty()) {
            throw new LuffyException(
                    "Hey! A todo task needs a description, you know! I can't remember nothing!");
        }
    }

    /**
     * Validates deadline command input.
     */
    public static void validateDeadlineCommand(String input) throws LuffyException {
        if (input.trim().length() <= 8 || input.substring(8).trim().isEmpty()) {
            throw new LuffyException(
                    "Oi! You gotta tell me what the deadline is for! I'm not a mind reader!");
        }

        int byIndex = input.indexOf("/by");
        if (byIndex == -1) {
            throw new LuffyException(
                    "Where's the '/by' part? I need to know when this deadline is, dattebayo!");
        }

        if (byIndex + 3 >= input.length() || input.substring(byIndex + 3).trim().isEmpty()) {
            throw new LuffyException(
                    "You forgot to tell me WHEN the deadline is! Put something after '/by'!");
        }
    }

    /**
     * Validates event command input.
     */
    public static void validateEventCommand(String input) throws LuffyException {
        if (input.trim().length() <= 5 || input.substring(5).trim().isEmpty()) {
            throw new LuffyException(
                    "What event are we talking about? Give me a description, nakama!");
        }

        int fromIndex = input.indexOf("/from");
        int toIndex = input.indexOf("/to");

        if (fromIndex == -1) {
            throw new LuffyException(
                    "Missing '/from'! When does this event start? I need to know!");
        }

        if (toIndex == -1) {
            throw new LuffyException(
                    "Missing '/to'! When does this event end? Don't leave me hanging!");
        }

        if (fromIndex >= toIndex) {
            throw new LuffyException(
                    "Hey! '/from' should come before '/to'! That's just common sense!");
        }

        if (fromIndex + 5 >= input.length()
                || input.substring(fromIndex + 5, toIndex).trim().isEmpty()) {
            throw new LuffyException(
                    "You didn't tell me when it starts! Put something after '/from'!");
        }

        if (toIndex + 3 >= input.length() || input.substring(toIndex + 3).trim().isEmpty()) {
            throw new LuffyException("You didn't tell me when it ends! Put something after '/to'!");
        }
    }

    /**
     * Validates mark/unmark command input.
     */
    public static void validateMarkUnmarkCommand(String input, boolean isMark, int taskCount)
            throws LuffyException {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            String action = isMark ? "mark" : "unmark";
            throw new LuffyException(
                    "Which task do you want me to " + action + "? Give me a number!");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new LuffyException("Task " + taskNumber + "? That doesn't exist! I only have "
                        + taskCount + " tasks!");
            }
        } catch (NumberFormatException e) {
            throw new LuffyException(
                    "'" + parts[1] + "' is not a number! Give me a proper task number!");
        }
    }

    /**
     * Validates delete command input.
     */
    public static void validateDeleteCommand(String input, int taskCount) throws LuffyException {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            throw new LuffyException("Which task do you want me to delete? Give me a number!");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new LuffyException("Task " + taskNumber + "? That doesn't exist! I only have "
                        + taskCount + " tasks!");
            }
        } catch (NumberFormatException e) {
            throw new LuffyException(
                    "'" + parts[1] + "' is not a number! Give me a proper task number!");
        }
    }

    /**
     * Parses user input and returns the appropriate Command object. Supports various command types
     * including todo, deadline, event, mark, unmark, delete, list, due, and bye commands. Command
     * parsing is case-insensitive.
     *
     * @param fullCommand the complete user input command string
     * @return Command object corresponding to the user's input
     * @throws LuffyException if the command is unknown or has invalid format
     */
    public static Command parse(String fullCommand) throws LuffyException {
        String input = fullCommand.trim();

        // If input is "bye", return ExitCommand
        if (input.equals("bye") || input.equals("Bye") || input.equals("BYE")) {
            return new ExitCommand();
        }

        // If input is "list", return ListCommand
        if (input.equals("list") || input.equals("List") || input.equals("LIST")) {
            return new ListCommand();
        }

        // If input starts with "todo", return AddTodoCommand
        if (input.startsWith("todo") || input.startsWith("Todo") || input.startsWith("TODO")) {
            validateTodoCommand(input);
            String description = input.substring(4).trim();
            return new AddTodoCommand(description);
        }

        // If input starts with "deadline", return AddDeadlineCommand
        if (input.startsWith("deadline") || input.startsWith("Deadline")
                || input.startsWith("DEADLINE")) {
            validateDeadlineCommand(input);
            int byIndex = input.indexOf("/by");
            String description = input.substring(8, byIndex).trim();
            String byStr = input.substring(byIndex + 3).trim();
            return new AddDeadlineCommand(description, byStr);
        }

        // If input starts with "event", return AddEventCommand
        if (input.startsWith("event") || input.startsWith("Event") || input.startsWith("EVENT")) {
            validateEventCommand(input);
            int fromIndex = input.indexOf("/from");
            int toIndex = input.indexOf("/to");
            String description = input.substring(5, fromIndex).trim();
            String fromStr = input.substring(fromIndex + 5, toIndex).trim();
            String toStr = input.substring(toIndex + 3).trim();
            return new AddEventCommand(description, fromStr, toStr);
        }

        // If input starts with "mark", return MarkCommand
        if (input.startsWith("mark") || input.startsWith("Mark") || input.startsWith("MARK")) {
            // We need to pass taskCount for validation, but we don't have access to it here
            // We'll validate in the command execution instead
            String[] parts = input.split(" ");
            if (parts.length < 2) {
                throw new LuffyException("Which task do you want me to mark? Give me a number!");
            }
            try {
                int taskNumber = Integer.parseInt(parts[1]);
                return new MarkCommand(taskNumber);
            } catch (NumberFormatException e) {
                throw new LuffyException(
                        "'" + parts[1] + "' is not a number! Give me a proper task number!");
            }
        }

        // If input starts with "unmark", return UnmarkCommand
        if (input.startsWith("unmark") || input.startsWith("Unmark")
                || input.startsWith("UNMARK")) {
            String[] parts = input.split(" ");
            if (parts.length < 2) {
                throw new LuffyException("Which task do you want me to unmark? Give me a number!");
            }
            try {
                int taskNumber = Integer.parseInt(parts[1]);
                return new UnmarkCommand(taskNumber);
            } catch (NumberFormatException e) {
                throw new LuffyException(
                        "'" + parts[1] + "' is not a number! Give me a proper task number!");
            }
        }

        // If input starts with "delete", return DeleteCommand
        if (input.startsWith("delete") || input.startsWith("Delete")
                || input.startsWith("DELETE")) {
            String[] parts = input.split(" ");
            if (parts.length < 2) {
                throw new LuffyException("Which task do you want me to delete? Give me a number!");
            }
            try {
                int taskNumber = Integer.parseInt(parts[1]);
                return new DeleteCommand(taskNumber);
            } catch (NumberFormatException e) {
                throw new LuffyException(
                        "'" + parts[1] + "' is not a number! Give me a proper task number!");
            }
        }

        // If input starts with "due", return DueCommand
        if (input.startsWith("due") || input.startsWith("Due") || input.startsWith("DUE")) {
            String[] parts = input.split(" ", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                throw new LuffyException("Which date do you want to check? Use: due 2019-12-02");
            }
            String dateStr = parts[1].trim();
            return new DueCommand(dateStr);
        }

        // If input starts with "find", return FindCommand
        if (input.startsWith("find") || input.startsWith("Find") || input.startsWith("FIND")) {
            String[] parts = input.split(" ");
            if (parts.length < 2) {
                throw new LuffyException("What do you want to find? Give me some keywords!");
            }

            // Extract keywords (skip the "find" command word)
            String[] keywords = new String[parts.length - 1];
            for (int i = 1; i < parts.length; i++) {
                keywords[i - 1] = parts[i].trim();
            }

            // Check if all keywords are empty or whitespace
            boolean hasValidKeyword = false;
            for (String keyword : keywords) {
                if (!keyword.isEmpty()) {
                    hasValidKeyword = true;
                    break;
                }
            }

            if (!hasValidKeyword) {
                throw new LuffyException("What do you want to find? Give me some keywords!");
            }

            return new FindCommand(keywords);
        }

        // If we get here, it's an unknown command
        if (!input.isEmpty()) {
            throw new LuffyException("I don't understand '" + input
                    + "'! Try: todo, deadline, event, mark, unmark, delete, list, due, find, or bye!");
        }

        // Empty input - just return null or handle as needed
        throw new LuffyException("Please enter a command!");
    }
}
