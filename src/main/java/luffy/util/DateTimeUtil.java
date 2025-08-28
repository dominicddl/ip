package luffy.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date/time formatting operations. Provides methods for converting LocalDateTime
 * objects to user-friendly display formats and for serializing/deserializing dates for file
 * storage.
 */
public class DateTimeUtil {

    /**
     * Formats LocalDateTime for display in a user-friendly format. Uses smart formatting: shows
     * date only if time is 23:59 (indicating date-only input), otherwise shows full date and time.
     * 
     * Format examples: - Date only: "Oct 15 2019" - Date with time: "Oct 15 2019, 6:00 pm"
     *
     * @param dateTime the LocalDateTime to format
     * @return formatted date/time string for user display
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime.getHour() == 23 && dateTime.getMinute() == 59) {
            // This was likely a date-only input, show just the date
            return dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        } else {
            // Show full date and time
            return dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a"));
        }
    }

    /**
     * Formats LocalDateTime for file storage using ISO format. The ISO format ensures consistent,
     * unambiguous storage that can be parsed reliably.
     *
     * @param dateTime the LocalDateTime to format for storage
     * @return ISO formatted date/time string (e.g., "2019-10-15T18:00:00")
     */
    public static String formatDateTimeForFile(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Parses LocalDateTime from file storage ISO format. Used to reconstruct LocalDateTime objects
     * when loading tasks from file.
     *
     * @param dateTimeStr the ISO formatted date/time string from file
     * @return LocalDateTime object parsed from the string
     * @throws java.time.format.DateTimeParseException if the string cannot be parsed
     */
    public static LocalDateTime parseDateTimeFromFile(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
