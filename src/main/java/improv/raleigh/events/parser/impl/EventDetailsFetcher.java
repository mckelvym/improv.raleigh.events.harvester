package improv.raleigh.events.parser.impl;

import static improv.raleigh.events.parser.impl.CssSelectors.BIO_TEXT;
import static improv.raleigh.events.parser.impl.CssSelectors.CONTENT;
import static improv.raleigh.events.parser.impl.CssSelectors.META_DESCRIPTION;
import static improv.raleigh.events.parser.impl.CssSelectors.META_OG_IMAGE;
import static improv.raleigh.events.parser.impl.CssSelectors.META_TWITTER_IMAGE;
import static improv.raleigh.events.parser.impl.CssSelectors.PAGE_LOAD_SELECTOR;
import static improv.raleigh.events.parser.impl.HtmlConstants.EMPTY;

import improv.raleigh.events.webdriver.PageLoader;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fetches and parses detailed information from individual event pages.
 * Extracts enhanced descriptions and higher quality images.
 */
public record EventDetailsFetcher(PageLoader pageLoader) {
    private static final Logger LOG
        = LoggerFactory.getLogger(EventDetailsFetcher.class);

    /**
     * Extracts the enhanced description from the event page.
     * Tries multiple sources: bio text, meta description.
     *
     * @param doc the event page document
     * @return the enhanced description, or empty string if not found
     */
    private String extractEnhancedDescription(final Document doc) {
        // Try to get bio text first (most detailed)
        final Element bioText = doc.selectFirst(BIO_TEXT);
        if (bioText != null) {
            final String text = bioText.text().trim();
            if (!text.isEmpty()) {
                return text;
            }
        }

        // Fall back to meta description
        final Element metaDesc = doc.selectFirst(META_DESCRIPTION);
        if (metaDesc != null) {
            final String content = metaDesc.attr(CONTENT).trim();
            if (!content.isEmpty()) {
                return content;
            }
        }

        return EMPTY;
    }

    /**
     * Extracts a higher quality image URL from the event page.
     * Looks for og:image meta tag which typically has higher resolution.
     *
     * @param doc the event page document
     * @return the high quality image URL, or empty string if not found
     */
    private String extractHighQualityImageUrl(final Document doc) {
        // Try og:image meta tag
        final Element ogImage = doc.selectFirst(META_OG_IMAGE);
        if (ogImage != null) {
            final String content = ogImage.attr(CONTENT).trim();
            if (!content.isEmpty()) {
                return content;
            }
        }

        // Try twitter:image meta tag
        final Element twitterImage = doc.selectFirst(META_TWITTER_IMAGE);
        if (twitterImage != null) {
            final String content = twitterImage.attr(CONTENT).trim();
            if (!content.isEmpty()) {
                return content;
            }
        }

        return "";
    }

    /**
     * Fetches enhanced details for an event from its detail page.
     *
     * @param eventUrl the URL of the event detail page
     * @return the event details, or null if unable to fetch
     */
    public EventDetails fetchEventDetails(final String eventUrl) {
        try {
            final Document doc = pageLoader.loadPage(eventUrl,
                PAGE_LOAD_SELECTOR);

            final String enhancedDescription = extractEnhancedDescription(doc);
            final String highQualityImageUrl = extractHighQualityImageUrl(doc);

            return new EventDetails(enhancedDescription, highQualityImageUrl);
        } catch (Exception e) {
            LOG.error("Error fetching event details from: {}", eventUrl, e);
            return null;
        }
    }

    /**
     * Container for event detail information.
     */
    public record EventDetails(String enhancedDescription, String highQualityImageUrl) {
        /**
         * Creates new EventDetails.
         *
         * @param enhancedDescription the enhanced description text
         * @param highQualityImageUrl the high quality image URL
         */
        public EventDetails(final String enhancedDescription,
                            final String highQualityImageUrl) {
            this.enhancedDescription = enhancedDescription != null
                ? enhancedDescription : EMPTY;
            this.highQualityImageUrl = highQualityImageUrl != null
                ? highQualityImageUrl : EMPTY;
        }
    }
}
