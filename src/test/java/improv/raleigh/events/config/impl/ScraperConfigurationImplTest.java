package improv.raleigh.events.config.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ScraperConfigurationImpl.
 */
class ScraperConfigurationImplTest {

    private ScraperConfigurationImpl config;

    @BeforeEach
    void setUp() {
        config = new ScraperConfigurationImpl();
    }

    @Test
    void testGetBaseUrl() {
        assertThat(config.getBaseUrl())
            .isEqualTo("https://improv.com/raleigh/calendar/");
    }

    @Test
    void testGetEventLinkBaseUrl() {
        assertThat(config.getEventLinkBaseUrl())
            .isEqualTo("https://improv.com");
    }

    @Test
    void testGetFeedDescription() {
        assertThat(config.getFeedDescription())
            .isEqualTo("Comedy shows and events at Improv Raleigh");
    }

    @Test
    void testGetFeedLink() {
        assertThat(config.getFeedLink())
            .isEqualTo("https://improv.com/raleigh/calendar/");
    }

    @Test
    void testGetFeedTitle() {
        assertThat(config.getFeedTitle())
            .isEqualTo("Improv Raleigh Events");
    }

    @Test
    void testGetMaxPages() {
        assertThat(config.getMaxPages())
            .isEqualTo(20);
    }

    @Test
    void testGetPageLoadTimeout() {
        assertThat(config.getPageLoadTimeout().toSecondsPart())
            .isEqualTo(10);
    }

    @Test
    void testGetRetentionDays() {
        assertThat(config.getRetentionDays())
            .isEqualTo(7);
    }

    @Test
    void testGetUserAgent() {
        assertThat(config.getUserAgent())
            .isEqualTo("Mozilla/5.0 (compatible; EventHarvester/1.0)");
    }

    @Test
    void testShouldFetchEnhancedDetails() {
        assertThat(config.shouldFetchEnhancedDetails())
            .isTrue();
    }
}
