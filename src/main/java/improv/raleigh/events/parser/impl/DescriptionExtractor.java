package improv.raleigh.events.parser.impl;

import static improv.raleigh.events.parser.impl.CssSelectors.TIMES;
import static improv.raleigh.events.parser.impl.HtmlConstants.EMPTY;
import static java.util.Objects.requireNonNull;

import org.jsoup.nodes.Element;

/**
 * Extracts event description or time information from HTML.
 */
public final class DescriptionExtractor {

    /**
     * Extracts the description from an event element.
     *
     * @param element the event element
     * @return the extracted description, or empty string if not found
     */
    public String extract(Element element) {
        requireNonNull(element, "element must not be null");
        Element timesDiv = element.selectFirst(TIMES);
        if (timesDiv == null) {
            return EMPTY;
        }
        return timesDiv.text().trim();
    }
}
