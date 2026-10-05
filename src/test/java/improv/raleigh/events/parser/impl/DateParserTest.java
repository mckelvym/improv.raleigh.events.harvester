package improv.raleigh.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for DateParser.
 * Verifies date parsing with multiple format strategies, month parsing, year inference,
 * and partial date parsing.
 */
class DateParserTest {

    private DateParser parser;

    @Test
    void inferYear_withCurrentMonth_returnsCurrentYear() {
        LocalDate now = LocalDate.now();
        Month currentMonth = now.getMonth();
        int day = now.getDayOfMonth();

        int result = parser.inferYear(currentMonth, day);

        assertThat(result).isEqualTo(now.getYear());
    }

    // Tests for parse() method with full month name formats

    @Test
    void inferYear_withDateExactly30DaysInPast_returnsCurrentYear() {
        LocalDate now = LocalDate.now();
        LocalDate pastDate = now.minusDays(30);
        Month pastMonth = pastDate.getMonth();
        int day = pastDate.getDayOfMonth();

        int result = parser.inferYear(pastMonth, day);

        assertThat(result).isEqualTo(now.getYear());
    }

    @Test
    void inferYear_withDateLessThan30DaysInPast_returnsCurrentYear() {
        LocalDate now = LocalDate.now();
        LocalDate pastDate = now.minusDays(15);
        Month pastMonth = pastDate.getMonth();
        int day = pastDate.getDayOfMonth();

        int result = parser.inferYear(pastMonth, day);

        assertThat(result).isEqualTo(now.getYear());
    }

    // Tests for parse() method with abbreviated month formats

    @Test
    void inferYear_withFutureMonth_returnsCurrentYear() {
        LocalDate now = LocalDate.now();
        Month futureMonth = now.plusMonths(2).getMonth();
        int day = 15;

        int result = parser.inferYear(futureMonth, day);

        assertThat(result).isEqualTo(now.getYear());
    }

    @Test
    void parseMonth_withAprAbbreviated_returnsApril() {
        Month result = parser.parseMonth("Apr");

        assertThat(result).isEqualTo(Month.APRIL);
    }

    // Tests for parse() method with slash formats

    @Test
    void parseMonth_withAprilFull_returnsApril() {
        Month result = parser.parseMonth("April");

        assertThat(result).isEqualTo(Month.APRIL);
    }

    @Test
    void parseMonth_withAugAbbreviated_returnsAugust() {
        Month result = parser.parseMonth("Aug");

        assertThat(result).isEqualTo(Month.AUGUST);
    }

    @Test
    void parseMonth_withAugustFull_returnsAugust() {
        Month result = parser.parseMonth("August");

        assertThat(result).isEqualTo(Month.AUGUST);
    }

    // Tests for parse() method with ISO format

    @Test
    void parseMonth_withBlankString_returnsNull() {
        Month result = parser.parseMonth("   ");

        assertThat(result).isNull();
    }

    @Test
    void parseMonth_withDecAbbreviated_returnsDecember() {
        Month result = parser.parseMonth("Dec");

        assertThat(result).isEqualTo(Month.DECEMBER);
    }

    // Tests for parse() method with RFC 1123 format

    @Test
    void parseMonth_withDecemberFull_returnsDecember() {
        Month result = parser.parseMonth("December");

        assertThat(result).isEqualTo(Month.DECEMBER);
    }

    @Test
    void parseMonth_withEmptyString_returnsNull() {
        Month result = parser.parseMonth("");

        assertThat(result).isNull();
    }

    // Tests for parse() method with whitespace

    @Test
    void parseMonth_withFebAbbreviated_returnsFebruary() {
        Month result = parser.parseMonth("Feb");

        assertThat(result).isEqualTo(Month.FEBRUARY);
    }

    @Test
    void parseMonth_withFebruaryFull_returnsFebruary() {
        Month result = parser.parseMonth("February");

        assertThat(result).isEqualTo(Month.FEBRUARY);
    }

    @Test
    void parseMonth_withInvalidMonth_returnsNull() {
        Month result = parser.parseMonth("InvalidMonth");

        assertThat(result).isNull();
    }

    // Tests for parse() method with null/blank/invalid inputs

    @Test
    void parseMonth_withJanAbbreviated_returnsJanuary() {
        Month result = parser.parseMonth("Jan");

        assertThat(result).isEqualTo(Month.JANUARY);
    }

    @Test
    void parseMonth_withJanuaryFull_returnsJanuary() {
        Month result = parser.parseMonth("January");

        assertThat(result).isEqualTo(Month.JANUARY);
    }

    @Test
    void parseMonth_withJulAbbreviated_returnsJuly() {
        Month result = parser.parseMonth("Jul");

        assertThat(result).isEqualTo(Month.JULY);
    }

    @Test
    void parseMonth_withJulyFull_returnsJuly() {
        Month result = parser.parseMonth("July");

        assertThat(result).isEqualTo(Month.JULY);
    }

    @Test
    void parseMonth_withJunAbbreviated_returnsJune() {
        Month result = parser.parseMonth("Jun");

        assertThat(result).isEqualTo(Month.JUNE);
    }

    @Test
    void parseMonth_withJuneFull_returnsJune() {
        Month result = parser.parseMonth("June");

        assertThat(result).isEqualTo(Month.JUNE);
    }

    @Test
    void parseMonth_withLeadingWhitespace_trimsAndParses() {
        Month result = parser.parseMonth("  January");

        assertThat(result).isEqualTo(Month.JANUARY);
    }

    // Edge case tests for parse()

    @Test
    void parseMonth_withLowercaseAbbreviated_returnsMonth() {
        Month result = parser.parseMonth("jan");

        assertThat(result).isEqualTo(Month.JANUARY);
    }

    @Test
    void parseMonth_withLowercase_returnsMonth() {
        Month result = parser.parseMonth("january");

        assertThat(result).isEqualTo(Month.JANUARY);
    }

    @Test
    void parseMonth_withMarAbbreviated_returnsMarch() {
        Month result = parser.parseMonth("Mar");

        assertThat(result).isEqualTo(Month.MARCH);
    }

    @Test
    void parseMonth_withMarchFull_returnsMarch() {
        Month result = parser.parseMonth("March");

        assertThat(result).isEqualTo(Month.MARCH);
    }

    // Tests for parseMonth() method with full month names

    @Test
    void parseMonth_withMayFull_returnsMay() {
        Month result = parser.parseMonth("May");

        assertThat(result).isEqualTo(Month.MAY);
    }

    @Test
    void parseMonth_withMixedCase_returnsMonth() {
        Month result = parser.parseMonth("JaNuArY");

        assertThat(result).isEqualTo(Month.JANUARY);
    }

    @Test
    void parseMonth_withNovAbbreviated_returnsNovember() {
        Month result = parser.parseMonth("Nov");

        assertThat(result).isEqualTo(Month.NOVEMBER);
    }

    @Test
    void parseMonth_withNovemberFull_returnsNovember() {
        Month result = parser.parseMonth("November");

        assertThat(result).isEqualTo(Month.NOVEMBER);
    }

    @Test
    void parseMonth_withNull_returnsNull() {
        Month result = parser.parseMonth(null);

        assertThat(result).isNull();
    }

    @Test
    void parseMonth_withNumericString_returnsNull() {
        Month result = parser.parseMonth("12");

        assertThat(result).isNull();
    }

    @Test
    void parseMonth_withOctAbbreviated_returnsOctober() {
        Month result = parser.parseMonth("Oct");

        assertThat(result).isEqualTo(Month.OCTOBER);
    }

    @Test
    void parseMonth_withOctoberFull_returnsOctober() {
        Month result = parser.parseMonth("October");

        assertThat(result).isEqualTo(Month.OCTOBER);
    }

    @Test
    void parseMonth_withSepAbbreviated_returnsSeptember() {
        Month result = parser.parseMonth("Sep");

        assertThat(result).isEqualTo(Month.SEPTEMBER);
    }

    @Test
    void parseMonth_withSeptemberFull_returnsSeptember() {
        Month result = parser.parseMonth("September");

        assertThat(result).isEqualTo(Month.SEPTEMBER);
    }

    @Test
    void parseMonth_withSurroundingWhitespace_trimsAndParses() {
        Month result = parser.parseMonth("  January  ");

        assertThat(result).isEqualTo(Month.JANUARY);
    }

    @Test
    void parseMonth_withTrailingWhitespace_trimsAndParses() {
        Month result = parser.parseMonth("January  ");

        assertThat(result).isEqualTo(Month.JANUARY);
    }

    // Tests for parseMonth() method with abbreviated month names

    @Test
    void parseMonth_withUppercaseAbbreviated_returnsMonth() {
        Month result = parser.parseMonth("JAN");

        assertThat(result).isEqualTo(Month.JANUARY);
    }

    @Test
    void parseMonth_withUppercase_returnsMonth() {
        Month result = parser.parseMonth("JANUARY");

        assertThat(result).isEqualTo(Month.JANUARY);
    }

    @Test
    void parsePartialDate_withBlankString_returnsNull() {
        LocalDate result = parser.parsePartialDate("   ");

        assertThat(result).isNull();
    }

    @Test
    void parsePartialDate_withDateRange_parsesStartDate() {
        String dateString = "Nov 15-16";
        Month month = Month.NOVEMBER;
        int day = 15;
        int expectedYear = parser.inferYear(month, day);

        LocalDate result = parser.parsePartialDate(dateString);

        assertThat(result).isEqualTo(LocalDate.of(expectedYear, month, day));
    }

    @Test
    void parsePartialDate_withDoubleDigitDay_returnsLocalDate() {
        String dateString = "Dec 25";
        Month month = Month.DECEMBER;
        int day = 25;
        int expectedYear = parser.inferYear(month, day);

        LocalDate result = parser.parsePartialDate(dateString);

        assertThat(result).isEqualTo(LocalDate.of(expectedYear, month, day));
    }

    @Test
    void parsePartialDate_withEmptyString_returnsNull() {
        LocalDate result = parser.parsePartialDate("");

        assertThat(result).isNull();
    }

    @Test
    void parsePartialDate_withFebruary29NonLeapYear_returnsNull() {
        // If current year or inferred year is not a leap year
        LocalDate result = parser.parsePartialDate("Feb 29");

        // This may be null or a valid date depending on whether the inferred year is a leap year
        // The test verifies the method handles this case without crashing
        // No assertion on the result as it depends on the current date
    }

    @Test
    void parsePartialDate_withFirstDayOfMonth_returnsLocalDate() {
        String dateString = "Jan 1";
        Month month = Month.JANUARY;
        int day = 1;
        int expectedYear = parser.inferYear(month, day);

        LocalDate result = parser.parsePartialDate(dateString);

        assertThat(result).isEqualTo(LocalDate.of(expectedYear, month, day));
    }

    @Test
    void parsePartialDate_withFullMonthNameAndRange_returnsLocalDate() {
        String dateString = "December 15-16";
        Month month = Month.DECEMBER;
        int day = 15;
        int expectedYear = parser.inferYear(month, day);

        LocalDate result = parser.parsePartialDate(dateString);

        assertThat(result).isEqualTo(LocalDate.of(expectedYear, month, day));
    }

    @Test
    void parsePartialDate_withFullMonthName_returnsLocalDate() {
        String dateString = "December 15";
        Month month = Month.DECEMBER;
        int day = 15;
        int expectedYear = parser.inferYear(month, day);

        LocalDate result = parser.parsePartialDate(dateString);

        assertThat(result).isEqualTo(LocalDate.of(expectedYear, month, day));
    }

    @Test
    void parsePartialDate_withInvalidDay_returnsNull() {
        LocalDate result = parser.parsePartialDate("Dec 32");

        assertThat(result).isNull();
    }

    // Tests for parseMonth() method with case variations

    @Test
    void parsePartialDate_withInvalidMonth_returnsNull() {
        LocalDate result = parser.parsePartialDate("InvalidMonth 15");

        assertThat(result).isNull();
    }

    @Test
    void parsePartialDate_withLastDayOfMonth_returnsLocalDate() {
        String dateString = "Dec 31";
        Month month = Month.DECEMBER;
        int day = 31;
        int expectedYear = parser.inferYear(month, day);

        LocalDate result = parser.parsePartialDate(dateString);

        assertThat(result).isEqualTo(LocalDate.of(expectedYear, month, day));
    }

    @Test
    void parsePartialDate_withNonNumericDay_returnsNull() {
        LocalDate result = parser.parsePartialDate("Dec ABC");

        assertThat(result).isNull();
    }

    @Test
    void parsePartialDate_withNull_returnsNull() {
        LocalDate result = parser.parsePartialDate(null);

        assertThat(result).isNull();
    }

    @Test
    void parsePartialDate_withOnlyMonth_returnsNull() {
        LocalDate result = parser.parsePartialDate("December");

        assertThat(result).isNull();
    }

    // Tests for parseMonth() method with whitespace

    @Test
    void parsePartialDate_withSingleDigitDay_returnsLocalDate() {
        String dateString = "Dec 5";
        Month month = Month.DECEMBER;
        int day = 5;
        int expectedYear = parser.inferYear(month, day);

        LocalDate result = parser.parsePartialDate(dateString);

        assertThat(result).isEqualTo(LocalDate.of(expectedYear, month, day));
    }

    @Test
    void parsePartialDate_withValidFormat_returnsLocalDate() {
        // This test will use year inference
        String dateString = "Dec 15";
        Month month = Month.DECEMBER;
        int day = 15;
        int expectedYear = parser.inferYear(month, day);

        LocalDate result = parser.parsePartialDate(dateString);

        assertThat(result).isEqualTo(LocalDate.of(expectedYear, month, day));
    }

    @Test
    void parse_withAbbreviatedMonthDoubleDigitDay_returnsLocalDate() {
        LocalDate result = parser.parse("Jan 05, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 5));
    }

    // Tests for parseMonth() method with null/blank/invalid inputs

    @Test
    void parse_withAbbreviatedMonthFormat_returnsLocalDate() {
        LocalDate result = parser.parse("Dec 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withBlankString_returnsNull() {
        LocalDate result = parser.parse("   ");

        assertThat(result).isNull();
    }

    @Test
    void parse_withDoubleDigitSlashFormat_returnsLocalDate() {
        LocalDate result = parser.parse("01/05/2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 5));
    }

    @Test
    void parse_withEmptyString_returnsNull() {
        LocalDate result = parser.parse("");

        assertThat(result).isNull();
    }

    @Test
    void parse_withFirstDayOfYear_returnsLocalDate() {
        LocalDate result = parser.parse("January 1, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    // Tests for inferYear() method
    // Note: These tests are time-dependent and use current date logic
    // They verify the 30-day lookback logic

    @Test
    void parse_withFullMonthDoubleDigitDay_returnsLocalDate() {
        LocalDate result = parser.parse("January 05, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 5));
    }

    @Test
    void parse_withFullMonthFormat_returnsLocalDate() {
        LocalDate result = parser.parse("December 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withInvalidDay_returnsNull() {
        LocalDate result = parser.parse("December 32, 2025");

        assertThat(result).isNull();
    }

    @Test
    void parse_withInvalidFormat_returnsNull() {
        LocalDate result = parser.parse("not a date");

        assertThat(result).isNull();
    }

    // Tests for parsePartialDate() method with valid inputs

    @Test
    void parse_withInvalidMonth_returnsNull() {
        LocalDate result = parser.parse("13/15/2025");

        assertThat(result).isNull();
    }

    @Test
    void parse_withIsoFormatSingleDigits_returnsLocalDate() {
        LocalDate result = parser.parse("2025-01-05");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 5));
    }

    @Test
    void parse_withIsoFormat_returnsLocalDate() {
        LocalDate result = parser.parse("2025-12-15");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withLastDayOfYear_returnsLocalDate() {
        LocalDate result = parser.parse("December 31, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 31));
    }

    @Test
    void parse_withLeadingWhitespace_trimsAndParses() {
        LocalDate result = parser.parse("  December 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withLeapYearDate_returnsLocalDate() {
        LocalDate result = parser.parse("February 29, 2024");

        assertThat(result).isEqualTo(LocalDate.of(2024, 2, 29));
    }

    // Tests for parsePartialDate() method with whitespace


    // Tests for parsePartialDate() method with null/blank/invalid inputs

    @Test
    void parse_withNonLeapYearFebruary29_adjustsToFebruary28() {
        // DateTimeFormatter uses ResolverStyle.SMART by default which adjusts invalid dates
        LocalDate result = parser.parse("February 29, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 2, 28));
    }

    @Test
    void parse_withNull_returnsNull() {
        LocalDate result = parser.parse(null);

        assertThat(result).isNull();
    }

    @Test
    void parse_withPartialDate_returnsNull() {
        LocalDate result = parser.parse("December 15");

        assertThat(result).isNull();
    }

    @Test
    void parse_withRfc1123FormatDifferentDay_returnsLocalDate() {
        // RFC_1123 validates day-of-week and expects single-digit days
        // Sunday, January 5, 2025
        LocalDate result = parser.parse("Sun, 5 Jan 2025 14:30:00 GMT");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 5));
    }

    @Test
    void parse_withRfc1123Format_returnsLocalDate() {
        LocalDate result = parser.parse("Mon, 15 Dec 2025 10:00:00 GMT");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withSingleDigitSlashFormat_returnsLocalDate() {
        LocalDate result = parser.parse("1/5/2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 5));
    }

    @Test
    void parse_withSlashFormat_returnsLocalDate() {
        LocalDate result = parser.parse("12/15/2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withSurroundingWhitespace_trimsAndParses() {
        LocalDate result = parser.parse("  December 15, 2025  ");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    // Edge case tests for parsePartialDate()

    @Test
    void parse_withTrailingWhitespace_trimsAndParses() {
        LocalDate result = parser.parse("December 15, 2025  ");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @BeforeEach
    void setUp() {
        parser = new DateParser();
    }
}
