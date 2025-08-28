package luffy.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class DateTimeUtilTest {

    @Test
    public void formatDateTime_regularDateTime_returnsFullFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 15, 14, 30);
        String result = DateTimeUtil.formatDateTime(dateTime);
        assertEquals("Dec 15 2024, 2:30 pm", result);
    }

    @Test
    public void formatDateTime_morningTime_returnsAMFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 5, 9, 15);
        String result = DateTimeUtil.formatDateTime(dateTime);
        assertEquals("Jan 05 2024, 9:15 am", result);
    }

    @Test
    public void formatDateTime_eveningTime_returnsPMFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 20, 18, 45);
        String result = DateTimeUtil.formatDateTime(dateTime);
        assertEquals("Jun 20 2024, 6:45 pm", result);
    }

    @Test
    public void formatDateTime_midnight_returnsFullFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 10, 0, 0);
        String result = DateTimeUtil.formatDateTime(dateTime);
        assertEquals("Mar 10 2024, 12:00 am", result);
    }

    @Test
    public void formatDateTime_noon_returnsFullFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 8, 25, 12, 0);
        String result = DateTimeUtil.formatDateTime(dateTime);
        assertEquals("Aug 25 2024, 12:00 pm", result);
    }

    @Test
    public void formatDateTime_defaultTime2359_returnsDateOnly() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 15, 23, 59);
        String result = DateTimeUtil.formatDateTime(dateTime);
        assertEquals("Dec 15 2024", result);
    }

    @Test
    public void formatDateTime_almostDefaultTime2358_returnsFullFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 15, 23, 58);
        String result = DateTimeUtil.formatDateTime(dateTime);
        assertEquals("Dec 15 2024, 11:58 pm", result);
    }

    @Test
    public void formatDateTime_leapYear_handlesCorrectly() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 2, 29, 10, 30);
        String result = DateTimeUtil.formatDateTime(dateTime);
        assertEquals("Feb 29 2024, 10:30 am", result);
    }

    @Test
    public void formatDateTimeForFile_regularDateTime_returnsISOFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 15, 14, 30, 45);
        String result = DateTimeUtil.formatDateTimeForFile(dateTime);
        assertEquals("2024-12-15T14:30:45", result);
    }

    @Test
    public void formatDateTimeForFile_withSeconds_includesSeconds() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 5, 9, 15, 30);
        String result = DateTimeUtil.formatDateTimeForFile(dateTime);
        assertEquals("2024-01-05T09:15:30", result);
    }

    @Test
    public void formatDateTimeForFile_withNanoseconds_includesNanoseconds() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 20, 18, 45, 30, 123456789);
        String result = DateTimeUtil.formatDateTimeForFile(dateTime);
        assertEquals("2024-06-20T18:45:30.123456789", result);
    }

    @Test
    public void formatDateTimeForFile_midnight_handlesCorrectly() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 10, 0, 0, 0);
        String result = DateTimeUtil.formatDateTimeForFile(dateTime);
        assertEquals("2024-03-10T00:00:00", result);
    }

    @Test
    public void parseDateTimeFromFile_validISOFormat_returnsCorrectDateTime() {
        String isoString = "2024-12-15T14:30:45";
        LocalDateTime result = DateTimeUtil.parseDateTimeFromFile(isoString);
        LocalDateTime expected = LocalDateTime.of(2024, 12, 15, 14, 30, 45);
        assertEquals(expected, result);
    }

    @Test
    public void parseDateTimeFromFile_withNanoseconds_parsesCorrectly() {
        String isoString = "2024-06-20T18:45:30.123456789";
        LocalDateTime result = DateTimeUtil.parseDateTimeFromFile(isoString);
        LocalDateTime expected = LocalDateTime.of(2024, 6, 20, 18, 45, 30, 123456789);
        assertEquals(expected, result);
    }

    @Test
    public void parseDateTimeFromFile_invalidFormat_throwsException() {
        String invalidString = "2024/12/15 14:30:45";
        assertThrows(DateTimeParseException.class, () -> {
            DateTimeUtil.parseDateTimeFromFile(invalidString);
        });
    }

    @Test
    public void parseDateTimeFromFile_emptyString_throwsException() {
        assertThrows(DateTimeParseException.class, () -> {
            DateTimeUtil.parseDateTimeFromFile("");
        });
    }

    @Test
    public void parseDateTimeFromFile_nullString_throwsException() {
        assertThrows(NullPointerException.class, () -> {
            DateTimeUtil.parseDateTimeFromFile(null);
        });
    }

    @Test
    public void roundTripConversion_formatAndParse_maintainsDateTime() {
        LocalDateTime original = LocalDateTime.of(2024, 12, 15, 14, 30, 45);
        String formatted = DateTimeUtil.formatDateTimeForFile(original);
        LocalDateTime parsed = DateTimeUtil.parseDateTimeFromFile(formatted);
        assertEquals(original, parsed);
    }

    @Test
    public void roundTripConversion_withNanoseconds_maintainsDateTime() {
        LocalDateTime original = LocalDateTime.of(2024, 6, 20, 18, 45, 30, 123456789);
        String formatted = DateTimeUtil.formatDateTimeForFile(original);
        LocalDateTime parsed = DateTimeUtil.parseDateTimeFromFile(formatted);
        assertEquals(original, parsed);
    }

    @Test
    public void formatDateTime_edgeCaseDates_handlesCorrectly() {
        // Test various edge cases
        LocalDateTime newYear = LocalDateTime.of(2024, 1, 1, 0, 0);
        assertEquals("Jan 01 2024, 12:00 am", DateTimeUtil.formatDateTime(newYear));

        LocalDateTime newYearEve = LocalDateTime.of(2023, 12, 31, 23, 59);
        assertEquals("Dec 31 2023", DateTimeUtil.formatDateTime(newYearEve));

        LocalDateTime christmas = LocalDateTime.of(2024, 12, 25, 12, 0);
        assertEquals("Dec 25 2024, 12:00 pm", DateTimeUtil.formatDateTime(christmas));
    }
}
