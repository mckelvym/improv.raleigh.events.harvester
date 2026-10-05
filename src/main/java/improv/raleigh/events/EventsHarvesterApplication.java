package improv.raleigh.events;

import improv.raleigh.events.config.ScraperConfiguration;
import improv.raleigh.events.config.impl.ScraperConfigurationImpl;
import improv.raleigh.events.domain.EventItem;
import improv.raleigh.events.feed.RssFeedManager;
import improv.raleigh.events.feed.RssFeedManagerImpl;
import improv.raleigh.events.parser.EventParser;
import improv.raleigh.events.parser.impl.EventParserImpl;
import improv.raleigh.events.scraper.EventScraper;
import improv.raleigh.events.scraper.impl.EventScraperImpl;
import improv.raleigh.events.webdriver.ChromeDriverManager;
import improv.raleigh.events.webdriver.PageLoader;
import improv.raleigh.events.webdriver.WebDriverManager;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Main application for scraping and generating RSS feed.
 */
public final class EventsHarvesterApplication {

    private static final String DEFAULT_OUTPUT_FILE = "events.xml";
    private static final Logger LOG
        = LoggerFactory.getLogger(EventsHarvesterApplication.class);

    private EventsHarvesterApplication() {
        // utility
    }

    private static void configureLogging() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * Main entry point.
     *
     * @param args command line arguments (optional output file path)
     */
    public static void main(final String[] args) {
        configureLogging();

        final String outputFile = args.length > 0
            ? args[0]
            : DEFAULT_OUTPUT_FILE;

        LOG.info("Starting Improv Raleigh Events Harvester");
        LOG.info("Output file: {}", outputFile);

        try {
            runHarvester(outputFile);
            LOG.info("Harvesting completed successfully");
        } catch (Exception e) {
            LOG.error("Harvesting failed", e);
            System.exit(1);
        }
    }

    private static void runHarvester(final String outputFile)
        throws Exception {
        final ScraperConfiguration config = new ScraperConfigurationImpl();

        final RssFeedManager feedManager = new RssFeedManagerImpl(config);

        LOG.info("Loading existing feed");
        final Set<String> existingGuids
            = feedManager.loadExistingGuids(outputFile);
        LOG.info("Found {} existing events", existingGuids.size());

        try (WebDriverManager driverManager = new ChromeDriverManager(config)) {
            final PageLoader pageLoader
                = new PageLoader(
                driverManager.getDriver(),
                config.getPageLoadTimeout()
            );
            final EventParser eventParser = new EventParserImpl(
                config,
                pageLoader,
                config.shouldFetchEnhancedDetails()
            );

            final EventScraper scraper = new EventScraperImpl(
                config,
                pageLoader,
                eventParser
            );

            LOG.info("Starting event scraping");
            final List<EventItem> newEvents
                = scraper.scrapeEvents(existingGuids);
            LOG.info("Scraped {} new events", newEvents.size());

            LOG.info("Generating RSS feed");
            feedManager.generateFeed(outputFile, newEvents, outputFile);

            LOG.info("RSS feed updated successfully");
        }
    }
}
