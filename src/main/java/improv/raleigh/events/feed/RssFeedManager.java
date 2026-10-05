package improv.raleigh.events.feed;

import improv.raleigh.events.domain.EventItem;
import java.util.List;
import java.util.Set;

/**
 * Interface for managing RSS feed generation and loading.
 */
public interface RssFeedManager {
    /**
     * Generates an RSS feed with new and existing events.
     *
     * @param filePath         path to write the RSS file
     * @param newEvents        list of newly scraped events
     * @param existingFilePath path to the existing feed (for merging)
     * @throws Exception if generation fails
     */
    void generateFeed(String filePath, List<EventItem> newEvents,
                      String existingFilePath)
        throws Exception;

    /**
     * Loads existing event GUIDs from the RSS file.
     *
     * @param filePath path to the RSS file
     * @return set of existing GUIDs
     * @throws Exception if loading fails
     */
    Set<String> loadExistingGuids(String filePath)
        throws Exception;
}
