package improv.raleigh.events.scraper.impl;


import static improv.raleigh.events.parser.impl.CssSelectors.EVENT_ITEM;
import static improv.raleigh.events.parser.impl.CssSelectors.MORE_SHOWS_BUTTON;
import static improv.raleigh.events.parser.impl.CssSelectors.PAGE_LOAD_SELECTOR;
import static improv.raleigh.events.parser.impl.HtmlConstants.HREF_ATTR;
import static improv.raleigh.events.parser.impl.HtmlConstants.HTTP;
import static java.util.Objects.requireNonNull;

import improv.raleigh.events.config.ScraperConfiguration;
import improv.raleigh.events.domain.EventItem;
import improv.raleigh.events.parser.EventParser;
import improv.raleigh.events.scraper.EventScraper;
import improv.raleigh.events.webdriver.PageLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scrapes events using loop-based pagination.
 * Implements the unified 3-phase flow: discover, filter, parse.
 */
public record EventScraperImpl(ScraperConfiguration config, PageLoader pageLoader,
                               EventParser eventParser) implements EventScraper {

    private static final Logger LOG = LoggerFactory.getLogger(EventScraperImpl.class);

    public EventScraperImpl {
        requireNonNull(config, "config must not be null");
        requireNonNull(pageLoader, "pageLoader must not be null");
        requireNonNull(eventParser, "eventParser must not be null");
    }

    /**
     * Phase 1: Discovers all event elements by paginating through the calendar.
     * Clicks "Load More" button up to MAX_PAGES times and collects event elements.
     *
     * @return map of event URLs to their elements
     */
    private Map<String, Element> discoverEventUrls() {
        final Map<String, Element> allEventElements = new LinkedHashMap<>();
        String nextUrl = config.getBaseUrl();
        int pageCount = 0;

        while (nextUrl != null && pageCount < config.getMaxPages()) {
            pageCount++;
            LOG.info("Discovering events on page {} from: {}", pageCount, nextUrl);

            final Document doc = pageLoader.loadPage(nextUrl, PAGE_LOAD_SELECTOR);
            final Elements eventElements = doc.select(EVENT_ITEM);
            LOG.info("Found {} events on page {}", eventElements.size(), pageCount);

            // Store event elements by their URL (to avoid duplicates)
            for (final Element eventElement : eventElements) {
                final String url = extractEventUrl(eventElement);
                if (!url.isEmpty()) {
                    allEventElements.put(url, eventElement);
                }
            }

            // Find next page URL
            nextUrl = findNextPageUrl(doc);
        }

        return allEventElements;
    }

    /**
     * Extracts and normalizes an event URL from an event element.
     *
     * @param eventElement the event element
     * @return the normalized event URL
     */
    private String extractEventUrl(final Element eventElement) {
        String href = eventElement.attr(HREF_ATTR);
        if (!href.isEmpty() && !href.startsWith(HTTP)) {
            href = config.getEventLinkBaseUrl() + href;
        }
        return href;
    }

    /**
     * Phase 2: Filters discovered event elements to identify new events.
     * Excludes events whose URLs match existing GUIDs.
     *
     * @param allEventElements map of all discovered event URLs to elements
     * @param existingGuids    set of GUIDs for events already in the feed
     * @return list of new event elements to parse
     */
    private List<Element> filterNewUrls(final Map<String, Element> allEventElements,
                                        final Set<String> existingGuids) {
        return allEventElements.entrySet().stream()
            .filter(entry -> !existingGuids.contains(entry.getKey()))
            .map(Map.Entry::getValue)
            .toList();
    }

    /**
     * Finds the next page URL from the "Load More" button.
     * Normalizes relative URLs to absolute URLs.
     *
     * @param doc the current page document
     * @return the next page URL, or null if no more pages
     */
    private String findNextPageUrl(final Document doc) {
        final Element moreShowsButton = doc.selectFirst(MORE_SHOWS_BUTTON);
        if (moreShowsButton != null) {
            final String href = moreShowsButton.attr(HREF_ATTR);
            if (!href.isEmpty()) {
                // Normalize URL
                if (href.startsWith("?")) {
                    return config.getBaseUrl() + href;
                } else if (!href.startsWith(HTTP)) {
                    return config.getEventLinkBaseUrl() + href;
                }
                return href;
            }
        }
        return null;
    }

    /**
     * Parses a single event and adds it to the collection.
     * Optionally fetches enhanced details from the event's detail page.
     *
     * @param eventElement the event element from the calendar listing
     * @param eventIndex   the current event number (1-based)
     * @param totalCount   the total number of events being parsed
     * @param newEvents    the collection to add parsed events to
     */
    private void parseAndAddEvent(final Element eventElement, final int eventIndex,
                                  final int totalCount, final List<EventItem> newEvents) {
        final Optional<EventItem> eventOpt = eventParser.parseEvent(eventElement);
        if (eventOpt.isPresent()) {
            final EventItem event = eventOpt.get();
            newEvents.add(event);
            LOG.info("Event {}/{}: {} ({})", eventIndex, totalCount, event.title(),
                event.eventDateStart());
        } else {
            final String url = extractEventUrl(eventElement);
            LOG.warn("Failed to parse event from element with URL: {}", url);
        }
    }

    /**
     * Phase 3: Parses new events from their elements.
     * Optionally fetches enhanced details from individual event pages.
     *
     * @param newEventElements list of new event elements to parse
     * @return list of parsed events
     */
    private List<EventItem> parseEvents(final List<Element> newEventElements) {
        final List<EventItem> events = new ArrayList<>();
        int eventCount = 0;

        for (final Element eventElement : newEventElements) {
            eventCount++;
            parseAndAddEvent(eventElement, eventCount, newEventElements.size(), events);
        }

        return events;
    }

    @Override
    public List<EventItem> scrapeEvents(final Set<String> existingGuids) {
        requireNonNull(existingGuids, "existingGuids must not be null");

        try {
            // Phase 1: Discover all event elements
            final Map<String, Element> allEventLinks = discoverEventUrls();
            LOG.info("Phase 1 complete: Discovered {} event links", allEventLinks.size());

            // Phase 2: Filter to new event elements
            final List<Element> newEventLinks = filterNewUrls(allEventLinks, existingGuids);
            LOG.info("Phase 2 complete: {} new events after filtering", newEventLinks.size());

            // Phase 3: Parse new events
            final List<EventItem> events = parseEvents(newEventLinks);
            LOG.info("Phase 3 complete: Parsed {} events", events.size());

            return events;
        } catch (final Exception e) {
            LOG.error("Unable to parse events", e);
            return List.of();
        }
    }
}
