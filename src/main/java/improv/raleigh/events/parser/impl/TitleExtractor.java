package improv.raleigh.events.parser.impl;

import static improv.raleigh.events.parser.impl.CssSelectors.ALL_HEADINGS;
import static improv.raleigh.events.parser.impl.CssSelectors.H3;
import static java.util.Objects.requireNonNull;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts event title from HTML using multiple strategies.
 */
public class TitleExtractor {
    private static final Logger LOG
        = LoggerFactory.getLogger(TitleExtractor.class);

    /**
     * Extracts the title from an event element.
     *
     * @param element the event element
     * @return the extracted title, or empty string if not found
     */
    public String extractTitle(final Element element) {
        String title = tryH3Title(element);
        if (title != null && !title.isBlank()) {
            return title;
        }

        title = tryHeadingTags(element);
        if (title != null && !title.isBlank()) {
            return title;
        }

        LOG.warn("Could not extract title from element");
        return "";
    }

    private String tryH3Title(final Element element) {
        final Element h3 = element.selectFirst(H3);
        if (h3 != null) {
            return h3.text().trim();
        }
        return null;
    }

    private String tryHeadingTags(final Element element) {
        final Elements headings = element.select(ALL_HEADINGS);
        if (!headings.isEmpty()) {
            return requireNonNull(headings.first()).text().trim();
        }
        return null;
    }
}
