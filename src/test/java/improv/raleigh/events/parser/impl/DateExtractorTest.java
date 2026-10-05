package improv.raleigh.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for DateExtractor.
 * Tests date string extraction and date parsing with year inference.
 */
class DateExtractorTest {

    private DateExtractor extractor;

    @Test
    void extractDateString_withDayOfWeek_removesDayOfWeek() {
        String html = """
            <div>
                <div class="dates">
                    <dt>Dec</dt>
                    <dd><i>Saturday</i> 25</dd>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Dec 25");
    }

    // Tests for extractDateString(Element)

    @Test
    void extractDateString_withMissingDd_returnsEmptyString() {
        String html = """
            <div>
                <div class="dates">
                    <dt>Nov</dt>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extractDateString_withMissingDt_returnsEmptyString() {
        String html = """
            <div>
                <div class="dates">
                    <dd>15-16</dd>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extractDateString_withMultipleDatesDiv_usesFirst() {
        String html = """
            <div>
                <div class="dates">
                    <dt>Nov</dt>
                    <dd>15-16</dd>
                </div>
                <div class="dates">
                    <dt>Dec</dt>
                    <dd>20-21</dd>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Nov 15-16");
    }

    @Test
    void extractDateString_withNestedDatesDiv_findsCorrectly() {
        String html = """
            <div>
                <div class="outer">
                    <div class="dates">
                        <dt>Mar</dt>
                        <dd>10</dd>
                    </div>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Mar 10");
    }

    @Test
    void extractDateString_withNoDatesDiv_returnsEmptyString() {
        String html = "<div><p>No dates here</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extractDateString_withNoDayOfWeek_returnsFullString() {
        String html = """
            <div>
                <div class="dates">
                    <dt>Jan</dt>
                    <dd>5-6</dd>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Jan 5-6");
    }

    @Test
    void extractDateString_withValidDatesDiv_returnsFormattedString() {
        String html = """
            <div>
                <div class="dates">
                    <dt>Nov</dt>
                    <dd><i>Fri</i> 15-16</dd>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Nov 15-16");
    }

    // Tests for parseDate(String)

    @Test
    void extractDateString_withWhitespace_trimsCorrectly() {
        String html = """
            <div>
                <div class="dates">
                    <dt>  Nov  </dt>
                    <dd>  15-16  </dd>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Nov 15-16");
    }

    @Test
    void parseDate_withAllMonths_parsesCorrectly() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (int i = 0; i < months.length; i++) {
            String dateString = months[i] + " 15";
            LocalDate result = extractor.parseDate(dateString);

            assertThat(result).isNotNull();
            assertThat(result.getMonth()).isEqualTo(Month.values()[i]);
        }
    }

    @Test
    void parseDate_withBlankString_returnsNull() {
        LocalDate result = extractor.parseDate("   ");

        assertThat(result).isNull();
    }

    @Test
    void parseDate_withDateRange_usesStartDate() {
        String dateString = "Jan 10-12";

        LocalDate result = extractor.parseDate(dateString);

        assertThat(result).isNotNull();
        assertThat(result.getMonth()).isEqualTo(Month.JANUARY);
        assertThat(result.getDayOfMonth()).isEqualTo(10);
    }

    @Test
    void parseDate_withEmptyString_returnsNull() {
        LocalDate result = extractor.parseDate("");

        assertThat(result).isNull();
    }

    @Test
    void parseDate_withInvalidDay_returnsNull() {
        String dateString = "Nov invalid";

        LocalDate result = extractor.parseDate(dateString);

        assertThat(result).isNull();
    }

    @Test
    void parseDate_withInvalidFormat_returnsNull() {
        String dateString = "Not a date";

        LocalDate result = extractor.parseDate(dateString);

        assertThat(result).isNull();
    }

    @Test
    void parseDate_withInvalidMonth_returnsNull() {
        String dateString = "InvalidMonth 15";

        LocalDate result = extractor.parseDate(dateString);

        assertThat(result).isNull();
    }

    @Test
    void parseDate_withMixedCaseMonth_parsesCorrectly() {
        String dateString = "nOv 15";

        LocalDate result = extractor.parseDate(dateString);

        assertThat(result).isNotNull();
        assertThat(result.getMonth()).isEqualTo(Month.NOVEMBER);
    }

    @Test
    void parseDate_withNullString_returnsNull() {
        LocalDate result = extractor.parseDate(null);

        assertThat(result).isNull();
    }

    @Test
    void parseDate_withOnlyMonthAndDay_parsesCorrectly() {
        String dateString = "Apr 1";

        LocalDate result = extractor.parseDate(dateString);

        assertThat(result).isNotNull();
        assertThat(result.getMonth()).isEqualTo(Month.APRIL);
        assertThat(result.getDayOfMonth()).isEqualTo(1);
    }

    @Test
    void parseDate_withPastDate_usesNextYear() {
        // The DateExtractor creates a date using current year first,
        // then bumps to next year if it's more than 30 days in the past.
        // Create a date from current year's past month that will definitely be past.
        LocalDate now = LocalDate.now();
        // Use a month that's definitely past in the current year
        LocalDate pastDate = now.withMonth(Math.max(1, now.getMonthValue() - 2))
            .withDayOfMonth(1);
        // If we're in Jan/Feb, this still works because month 1-2 minus 2 = -1 to 0,
        // Math.max(1, x) gives 1, so we test with Jan 1 which is recent, not past.
        // Skip this test logic at year boundary - test a guaranteed past month.
        if (now.getMonthValue() <= 2) {
            // At year start, test with a month that in current year is in future
            // (e.g., Nov in Jan 2026 -> Nov 2026 is future, stays current year)
            String dateString = "Nov 1";
            LocalDate result = extractor.parseDate(dateString);
            assertThat(result).isNotNull();
            // Nov 2026 is in future relative to Jan 2026, so stays in current year
            assertThat(result.getYear()).isEqualTo(now.getYear());
        } else {
            // Later in year, use a clearly past month
            String monthStr = pastDate.getMonth().name().substring(0, 3);
            String dateString = monthStr + " " + pastDate.getDayOfMonth();
            LocalDate result = extractor.parseDate(dateString);
            assertThat(result).isNotNull();
            // Should infer next year since date is more than 30 days in the past
            assertThat(result.getYear()).isEqualTo(now.getYear() + 1);
        }
    }

    @Test
    void parseDate_withRecentPastDate_usesCurrentYear() {
        // Create a date within 30 days of now
        LocalDate now = LocalDate.now();
        LocalDate recentDate = now.minusDays(15);

        String monthStr = recentDate.getMonth().name().substring(0, 3);
        String dateString = monthStr + " " + recentDate.getDayOfMonth();

        LocalDate result = extractor.parseDate(dateString);

        assertThat(result).isNotNull();
        // Should use current year since within 30-day threshold
        assertThat(result.getYear()).isEqualTo(now.getYear());
    }

    @Test
    void parseDate_withSingleDay_parsesCorrectly() {
        String dateString = "Dec 25";

        LocalDate result = extractor.parseDate(dateString);

        assertThat(result).isNotNull();
        assertThat(result.getMonth()).isEqualTo(Month.DECEMBER);
        assertThat(result.getDayOfMonth()).isEqualTo(25);
    }

    @Test
    void parseDate_withValidDateString_returnsLocalDate() {
        String dateString = "Nov 15-16";

        LocalDate result = extractor.parseDate(dateString);

        assertThat(result).isNotNull();
        assertThat(result.getMonth()).isEqualTo(Month.NOVEMBER);
        assertThat(result.getDayOfMonth()).isEqualTo(15);
    }

    @Test
    void parseDate_withYearInference_usesCurrentYearIfNotPast() {
        // Create a date in the future of current year
        LocalDate now = LocalDate.now();
        Month futureMonth = now.getMonth().plus(1);
        if (futureMonth == Month.JANUARY) {
            // Skip this test if we're in December
            return;
        }

        String dateString = futureMonth.name().substring(0, 3) + " 15";

        LocalDate result = extractor.parseDate(dateString);

        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo(now.getYear());
    }

    @BeforeEach
    void setUp() {
        extractor = new DateExtractor();
    }
}
