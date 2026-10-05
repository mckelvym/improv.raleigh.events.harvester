package improv.raleigh.events.config;

import java.time.Duration;

/**
 * Configuration interface for the event scraper.
 * Provides site-specific URLs and settings.
 */
public interface ScraperConfiguration {
    /**
     * Gets the base URL for the calendar page.
     *
     * @return the calendar URL
     */
    String getBaseUrl();

    /**
     * Gets the base URL for resolving relative event links.
     * This may differ from getBaseUrl() which is the calendar page URL.
     *
     * @return the base URL for event links
     */
    String getEventLinkBaseUrl();

    /**
     * Gets the RSS feed description.
     *
     * @return the feed description
     */
    String getFeedDescription();

    /**
     * Gets the RSS feed link.
     *
     * @return the feed link URL
     */
    String getFeedLink();

    /**
     * Gets the RSS feed title.
     *
     * @return the feed title
     */
    String getFeedTitle();

    /**
     * Gets the maximum number of pages to fetch during pagination.
     * Acts as a safety limit to prevent infinite loops.
     *
     * @return the maximum number of pages
     */
    int getMaxPages();

    /**
     * Gets the timeout duration for page loads.
     *
     * @return the timeout duration
     */
    Duration getPageLoadTimeout();

    /**
     * Gets the number of days to keep events in the feed.
     *
     * @return days to retain events
     */
    int getRetentionDays();

    /**
     * Gets the user agent string for web requests.
     *
     * @return the user agent string
     */
    String getUserAgent();

    /**
     * Determines whether to fetch enhanced details from individual event pages.
     * When enabled, the scraper will fetch each event's detail page to extract
     * additional information such as full descriptions and high-quality images.
     *
     * @return true to fetch enhanced details, false otherwise
     */
    boolean shouldFetchEnhancedDetails();
}
