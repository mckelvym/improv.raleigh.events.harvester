package improv.raleigh.events.parser.impl;

import static improv.raleigh.events.parser.impl.CssSelectors.DATES;
import static improv.raleigh.events.parser.impl.CssSelectors.DATE_DESCRIPTION;
import static improv.raleigh.events.parser.impl.CssSelectors.DATE_TERM;
import static improv.raleigh.events.parser.impl.CssSelectors.DAY_OF_WEEK;

import java.time.LocalDate;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts event date from HTML.
 */
public class DateExtractor {
    private static final String EMPTY = "";
    private static final Logger LOG
        = LoggerFactory.getLogger(DateExtractor.class);

    private final DateParser dateParser;

    public DateExtractor() {
        this.dateParser = new DateParser();
    }

    /**
     * Extracts the date string from an event element.
     *
     * @param eventElement the event element
     * @return the extracted date string, or empty string if not found
     */
    public String extractDateString(final Element eventElement) {
        final Element datesDiv = eventElement.selectFirst(DATES);
        if (datesDiv != null) {
            final Element dt = datesDiv.selectFirst(DATE_TERM);
            final Element dd = datesDiv.selectFirst(DATE_DESCRIPTION);

            if (dt != null && dd != null) {
                final String month = dt.text().trim();
                final Element dayOfWeek = dd.selectFirst(DAY_OF_WEEK);
                String dayRange = dd.text().trim();

                if (dayOfWeek != null) {
                    dayRange = dayRange.replace(dayOfWeek.text(), EMPTY)
                        .trim();
                }

                return month + " " + dayRange;
            }
        }

        LOG.warn("Could not extract date from element");
        return EMPTY;
    }

    /**
     * Parses a date string with year inference.
     * Delegates to DateParser for parsing logic.
     *
     * @param dateString date string like "Nov 15-16" or "Dec 4-6"
     * @return parsed LocalDate, or null if parsing fails
     */
    public LocalDate parseDate(final String dateString) {
        return dateParser.parsePartialDate(dateString);
    }
}
