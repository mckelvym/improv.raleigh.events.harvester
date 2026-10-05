package improv.raleigh.events.parser.impl;

import static improv.raleigh.events.parser.impl.HtmlConstants.HREF_ATTR;
import static improv.raleigh.events.parser.impl.HtmlConstants.HTTP;
import static java.util.Objects.requireNonNull;

import improv.raleigh.events.config.ScraperConfiguration;
import improv.raleigh.events.domain.EventItem;
import improv.raleigh.events.parser.EventParser;
import improv.raleigh.events.webdriver.PageLoader;
import java.util.Optional;
import javax.annotation.Nullable;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses and extracts event fields.
 */
public class EventParserImpl implements EventParser {
    private static final Logger LOG
        = LoggerFactory.getLogger(EventParserImpl.class);

    private final ScraperConfiguration config;
    private final DateExtractor dateExtractor;
    private final DescriptionExtractor descriptionExtractor;
    private final EventDetailsFetcher detailsFetcher;
    private final boolean fetchEnhancedDetails;
    private final ImageExtractor imageExtractor;
    private final TitleExtractor titleExtractor;

    /**
     * Creates a new EventParserImpl.
     *
     * @param config               the scraper configuration
     * @param pageLoader           the page loader for fetching event details
     * @param fetchEnhancedDetails whether to fetch enhanced details
     *                             from individual event pages
     */
    public EventParserImpl(final ScraperConfiguration config,
                           @Nullable final PageLoader pageLoader,
                           final boolean fetchEnhancedDetails) {
        this.config = requireNonNull(config, "config must not be null");
        this.titleExtractor = new TitleExtractor();
        this.dateExtractor = new DateExtractor();
        this.descriptionExtractor = new DescriptionExtractor();
        this.imageExtractor = new ImageExtractor();
        this.detailsFetcher = pageLoader != null
            ? new EventDetailsFetcher(pageLoader) : null;
        this.fetchEnhancedDetails = fetchEnhancedDetails;
    }

    private String extractId(final Element eventElement) {
        String id = eventElement.attr("id");
        if (id.startsWith("ev")) {
            id = id.substring(2);
        }
        return id;
    }

    private String extractUrl(final Element eventElement) {
        String href = eventElement.attr(HREF_ATTR);
        if (!href.isEmpty() && !href.startsWith(HTTP)) {
            href = config.getEventLinkBaseUrl() + href;
        }
        return href;
    }

    @Override
    public Optional<EventItem> parseEvent(final Element eventElement) {
        requireNonNull(eventElement, "eventElement must not be null");
        final String id = extractId(eventElement);
        final String url = extractUrl(eventElement);
        final String title = titleExtractor.extractTitle(eventElement);
        final String dateString = dateExtractor.extractDateString(eventElement);
        String imageUrl = imageExtractor.extractImageUrl(eventElement);
        String description
            = descriptionExtractor.extract(eventElement);

        if (id.isEmpty() || title.isEmpty() || url.isEmpty()) {
            LOG.warn("Skipping event with missing required fields");
            return Optional.empty();
        }

        // Fetch enhanced details from individual event page if enabled
        if (fetchEnhancedDetails && detailsFetcher != null) {
            final EventDetailsFetcher.EventDetails details
                = detailsFetcher.fetchEventDetails(url);
            if (details != null) {
                // Use enhanced description if available
                if (!details.enhancedDescription().isEmpty()) {
                    description = details.enhancedDescription();
                }
                // Use higher quality image if available
                if (!details.highQualityImageUrl().isEmpty()) {
                    imageUrl = details.highQualityImageUrl();
                }
            }
        }

        return Optional.of(new EventItem(
            id,
            title,
            url,
            description,
            dateExtractor.parseDate(dateString),
            null,  // eventDateEnd
            imageUrl,
            null   // location
        ));
    }
}
