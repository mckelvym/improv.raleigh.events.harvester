package improv.raleigh.events.parser.impl;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized date parsing utility
 */
public final class DateParser {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
        DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("M/d/yyyy", Locale.US),
        DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US),
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.RFC_1123_DATE_TIME
    );
    private static final Logger LOG = LoggerFactory.getLogger(DateParser.class);

    /**
     * Infers the year for a given month and day.
     * If the resulting date is more than 30 days in the past, assumes next year.
     * This is useful for parsing dates without year information.
     *
     * @param month the month
     * @param day   the day of month
     * @return the inferred year
     */
    public int inferYear(final Month month, final int day) {
        final LocalDate now = LocalDate.now();
        final int currentYear = now.getYear();
        LocalDate candidateDate = LocalDate.of(currentYear, month, day);

        if (candidateDate.isBefore(now.minusDays(30))) {
            return currentYear + 1;
        }
        return currentYear;
    }

    /**
     * Parses a date string to LocalDate using multiple format strategies.
     *
     * @param dateStr the date string to parse
     * @return the parsed LocalDate, or null if parsing fails
     */
    @Nullable
    public LocalDate parse(final String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }

        String trimmed = dateStr.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

        LOG.warn("Could not parse date '{}' with any known format", dateStr);
        return null;
    }

    /**
     * Parses a month name (short or full form) to a Month enum.
     *
     * @param monthStr the month text to parse (e.g., "Jan", "January")
     * @return the parsed Month, or null if parsing fails
     */
    @Nullable
    public Month parseMonth(final String monthStr) {
        if (monthStr == null || monthStr.isBlank()) {
            return null;
        }

        String trimmed = monthStr.trim();
        for (Month month : Month.values()) {
            String shortName = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String fullName = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            if (trimmed.equalsIgnoreCase(shortName) || trimmed.equalsIgnoreCase(fullName)) {
                return month;
            }
        }
        return null;
    }

    /**
     * Parses a date string with year inference.
     * Handles formats like "Nov 15-16" or "Dec 4-6".
     * If the month is remaining in current year, use current year.
     * Otherwise, use next year.
     *
     * @param dateString date string like "Nov 15-16" or "Dec 4-6"
     * @return parsed LocalDate, or null if parsing fails
     */
    @Nullable
    public LocalDate parsePartialDate(final String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }

        try {
            final String[] parts = dateString.split("\\s+");
            if (parts.length < 2) {
                return null;
            }

            final String monthStr = parts[0];
            final String dayPart = parts[1].split("-")[0];

            final Month month = parseMonth(monthStr);
            if (month == null) {
                return null;
            }

            final int day = Integer.parseInt(dayPart);
            final int year = inferYear(month, day);

            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            LOG.warn("Failed to parse partial date: {}", dateString, e);
            return null;
        }
    }
}
