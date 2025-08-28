package luffy.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date/time formatting operations.
 */
public class DateTimeUtil {

    /**
     * Formats LocalDateTime for display in a user-friendly format. Format: MMM dd yyyy, h:mm AM/PM
     * If time is 23:59 (default for date-only input), shows date only.
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
     * Formats LocalDateTime for file storage in ISO format.
     */
    public static String formatDateTimeForFile(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Parses LocalDateTime from file storage ISO format.
     */
    public static LocalDateTime parseDateTimeFromFile(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
