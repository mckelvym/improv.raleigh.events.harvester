package improv.raleigh.events.parser;

import improv.raleigh.events.domain.EventItem;
import java.util.Optional;
import org.jsoup.nodes.Element;

/**
 * Interface for parsing event information from HTML elements.
 */
public interface EventParser {

    /**
     * Parses an event from an HTML element.
     *
     * @param eventElement the event HTML element
     * @return Optional containing the parsed EventItem, or empty if event should be skipped
     * @throws RuntimeException if parsing fails due to unexpected error
     */
    Optional<EventItem> parseEvent(Element eventElement);
}
