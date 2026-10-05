package improv.raleigh.events.config.impl;

import improv.raleigh.events.config.ScraperConfiguration;
import java.time.Duration;

/**
 * Configuration for scraping Improv Raleigh events.
 */
public class ScraperConfigurationImpl implements ScraperConfiguration {
    private static final String BASE_URL
        = "https://improv.com/raleigh/calendar/";
    private static final String FEED_DESCRIPTION
        = "Comedy shows and events at Improv Raleigh";
    private static final String FEED_TITLE = "Improv Raleigh Events";
    private static final boolean FETCH_ENHANCED_DETAILS = true;
    private static final int MAX_PAGES = 20;
    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(10);
    private static final int RETENTION_DAYS = 7;
    private static final String USER_AGENT
        = "Mozilla/5.0 (compatible; EventHarvester/1.0)";

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public String getEventLinkBaseUrl() {
        return "https://improv.com";
    }

    @Override
    public String getFeedDescription() {
        return FEED_DESCRIPTION;
    }

    @Override
    public String getFeedLink() {
        return getBaseUrl();
    }

    @Override
    public String getFeedTitle() {
        return FEED_TITLE;
    }

    @Override
    public int getMaxPages() {
        return MAX_PAGES;
    }

    @Override
    public Duration getPageLoadTimeout() {
        return PAGE_LOAD_TIMEOUT;
    }

    @Override
    public int getRetentionDays() {
        return RETENTION_DAYS;
    }

    @Override
    public String getUserAgent() {
        return USER_AGENT;
    }

    @Override
    public boolean shouldFetchEnhancedDetails() {
        return FETCH_ENHANCED_DETAILS;
    }
}
