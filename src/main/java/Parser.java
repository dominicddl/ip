import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Deals with making sense of the user command and parsing date/time strings.
 */
public class Parser {

    /**
     * Parses a date/time string into LocalDateTime. Supports formats: - yyyy-mm-dd (date only, time
     * defaults to 23:59) - yyyy-mm-dd HHmm (date with time in 24-hour format) - yyyy-mm-dd HH:mm
     * (date with time in 24-hour format) - yyyy-mm-dd h:mm AM/PM (date with time in 12-hour format)
     * - d/m/yyyy (date only, time defaults to 23:59) - d/m/yyyy HHmm (date with time in 24-hour
     * format) - d/m/yyyy HH:mm (date with time in 24-hour format) - d/m/yyyy h:mm AM/PM (date with
     * time in 12-hour format)
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) throws LuffyException {
        dateTimeStr = dateTimeStr.trim();

        // Define possible formatters
        DateTimeFormatter[] formatters = {
                // yyyy-mm-dd formats
                DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                // d/m/yyyy formats
                DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
                DateTimeFormatter.ofPattern("d/M/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("d/M/yyyy h:mm a"),
                DateTimeFormatter.ofPattern("d/M/yyyy")};

        // Try parsing with each formatter
        for (DateTimeFormatter formatter : formatters) {
            try {
                if (dateTimeStr.matches(".*\\d{4}-\\d{2}-\\d{2}$")
                        || dateTimeStr.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}$")) {
                    // Date only formats - parse as LocalDate and set time to 23:59
                    LocalDate date = LocalDate.parse(dateTimeStr, formatter);
                    return date.atTime(23, 59);
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
     * Provides helpful suggestions for common date format mistakes.
     */
    private static String getDateFormatSuggestion(String invalidDate) {
        if (invalidDate.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
            return "Did you mean to use '/' instead of '-'? Try: " + invalidDate.replace("-", "/");
        }
        if (invalidDate.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
            return "For yyyy/mm/dd format, use '-' instead: " + invalidDate.replace("/", "-");
        }
        if (invalidDate.matches("\\d{1,2}/\\d{1,2}/\\d{2}")) {
            return "Use 4-digit year: " + invalidDate.substring(0, invalidDate.length() - 2) + "20"
                    + invalidDate.substring(invalidDate.length() - 2);
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
     * Parses user input and returns the appropriate Command object.
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

        // If we get here, it's an unknown command
        if (!input.isEmpty()) {
            throw new LuffyException("I don't understand '" + input
                    + "'! Try: todo, deadline, event, mark, unmark, delete, list, due, or bye!");
        }

        // Empty input - just return null or handle as needed
        throw new LuffyException("Please enter a command!");
    }
}
