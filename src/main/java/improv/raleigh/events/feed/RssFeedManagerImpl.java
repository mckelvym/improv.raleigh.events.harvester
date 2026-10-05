package improv.raleigh.events.feed;

import static improv.raleigh.events.feed.RssElementNames.CHANNEL;
import static improv.raleigh.events.feed.RssElementNames.DESCRIPTION;
import static improv.raleigh.events.feed.RssElementNames.ENCLOSURE;
import static improv.raleigh.events.feed.RssElementNames.ENCODING_UTF8;
import static improv.raleigh.events.feed.RssElementNames.EVENT_NAMESPACE_URI;
import static improv.raleigh.events.feed.RssElementNames.EV_ENDDATE;
import static improv.raleigh.events.feed.RssElementNames.EV_STARTDATE;
import static improv.raleigh.events.feed.RssElementNames.GUID;
import static improv.raleigh.events.feed.RssElementNames.IMAGE_JPEG_TYPE;
import static improv.raleigh.events.feed.RssElementNames.INDENT_AMOUNT;
import static improv.raleigh.events.feed.RssElementNames.IS_PERMALINK_ATTR;
import static improv.raleigh.events.feed.RssElementNames.ITEM;
import static improv.raleigh.events.feed.RssElementNames.LANGUAGE;
import static improv.raleigh.events.feed.RssElementNames.LANGUAGE_VALUE;
import static improv.raleigh.events.feed.RssElementNames.LAST_BUILD_DATE;
import static improv.raleigh.events.feed.RssElementNames.LINK;
import static improv.raleigh.events.feed.RssElementNames.PUB_DATE;
import static improv.raleigh.events.feed.RssElementNames.RSS;
import static improv.raleigh.events.feed.RssElementNames.RSS_VERSION;
import static improv.raleigh.events.feed.RssElementNames.TITLE;
import static improv.raleigh.events.feed.RssElementNames.TRUE_VALUE;
import static improv.raleigh.events.feed.RssElementNames.TYPE_ATTR;
import static improv.raleigh.events.feed.RssElementNames.URL_ATTR;
import static improv.raleigh.events.feed.RssElementNames.VERSION_ATTR;
import static improv.raleigh.events.feed.RssElementNames.XMLNS_EV_ATTR;
import static improv.raleigh.events.feed.RssElementNames.XSLT_INDENT_PROPERTY;
import static java.util.Objects.requireNonNull;

import improv.raleigh.events.config.ScraperConfiguration;
import improv.raleigh.events.domain.EventItem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of RssFeedManager.
 * Handles RSS feed generation with XXE protection.
 */
public class RssFeedManagerImpl implements RssFeedManager {
    private static final Logger LOG
        = LoggerFactory.getLogger(RssFeedManagerImpl.class);
    private final ScraperConfiguration config;
    private final EventFilter eventFilter;
    private final XmlSecurityConfigurer securityConfigurer;

    /**
     * Creates a new RssFeedManagerImpl.
     *
     * @param config the scraper configuration
     */
    public RssFeedManagerImpl(final ScraperConfiguration config) {
        this.config = requireNonNull(config, "config must not be null");
        this.eventFilter = new EventFilter(config);
        this.securityConfigurer = new XmlSecurityConfigurer();
    }

    private void addChannelMetadata(final Document doc,
                                    final Element channel) {
        appendTextElement(doc, channel, TITLE, config.getFeedTitle());
        appendTextElement(doc, channel, LINK, config.getFeedLink());
        appendTextElement(doc, channel, DESCRIPTION, config.getFeedDescription());
        appendTextElement(doc, channel, LANGUAGE, LANGUAGE_VALUE);
        appendTextElement(doc, channel, LAST_BUILD_DATE,
            ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    /**
     * Adds a description element wrapped in CDATA, omitting it when empty.
     *
     * @param doc         the XML document
     * @param item        the item element to add to
     * @param description the description HTML or text
     */
    private void addDescriptionElement(final Document doc, final Element item,
                                       final String description) {
        if (description.isEmpty()) {
            return;
        }
        final Element element = doc.createElement(DESCRIPTION);
        element.appendChild(doc.createCDATASection(description));
        item.appendChild(element);
    }

    /**
     * Adds the machine-readable event dates (RSS Event module) used for retention.
     *
     * @param doc   the XML document
     * @param item  the item element to add to
     * @param event the event whose dates to add
     */
    private void addEventDateElements(final Document doc, final Element item,
                                      final EventItem event) {
        final Element startDate = doc.createElement(EV_STARTDATE);
        startDate.setTextContent(event.eventDateStart().toString());
        item.appendChild(startDate);
        if (event.eventDateEnd() != null) {
            final Element endDate = doc.createElement(EV_ENDDATE);
            endDate.setTextContent(event.eventDateEnd().toString());
            item.appendChild(endDate);
        }
    }

    /**
     * Adds the item GUID, which is always the event URL and therefore a permalink.
     *
     * @param doc   the XML document
     * @param item  the item element to add to
     * @param event the event whose GUID to add
     */
    private void addGuidElement(final Document doc, final Element item, final EventItem event) {
        final Element guid = doc.createElement(GUID);
        guid.setAttribute(IS_PERMALINK_ATTR, TRUE_VALUE);
        guid.setTextContent(event.guid());
        item.appendChild(guid);
    }

    private void appendTextElement(final Document doc,
                                   final Element parent,
                                   final String tagName,
                                   final String text) {
        final Element element = doc.createElement(tagName);
        element.setTextContent(text);
        parent.appendChild(element);
    }

    private String buildDescription(final EventItem event) {
        final StringBuilder desc = new StringBuilder();

        if (event.eventDateStart() != null) {
            desc.append(event.eventDateStart());
        }

        if (!event.sanitizedDescription().isEmpty()) {
            if (!desc.isEmpty()) {
                desc.append(" - ");
            }
            desc.append(event.sanitizedDescription());
        }

        return desc.toString();
    }

    private String buildTitle(final EventItem event) {
        final StringBuilder title = new StringBuilder(event.title());
        if (event.eventDateStart() != null) {
            title.append(" (").append(event.eventDateStart()).append(")");
        }
        return title.toString();
    }

    private Element createItemElement(final Document doc,
                                      final EventItem event) {
        final Element item = doc.createElement(ITEM);

        final String title = buildTitle(event);
        appendTextElement(doc, item, TITLE, title);
        appendTextElement(doc, item, LINK, event.link());
        addGuidElement(doc, item, event);
        addEventDateElements(doc, item, event);

        final String description = buildDescription(event);
        addDescriptionElement(doc, item, description);

        // pubDate reflects the harvest time, not the event date
        appendTextElement(doc, item, PUB_DATE,
            ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));

        // Add image as enclosure element (RSS 2.0 standard)
        if (event.hasImage()) {
            Element enclosure = doc.createElement(ENCLOSURE);
            enclosure.setAttribute(URL_ATTR, event.imageUrl());
            enclosure.setAttribute(TYPE_ATTR, IMAGE_JPEG_TYPE);
            item.appendChild(enclosure);
        }

        return item;
    }

    @Override
    public void generateFeed(final String filePath,
                             final List<EventItem> newEvents,
                             final String existingFilePath)
        throws Exception {
        requireNonNull(filePath, "filePath must not be null");
        requireNonNull(newEvents, "newEvents must not be null");
        requireNonNull(existingFilePath, "existingFilePath must not be null");

        LOG.info("Generating RSS feed with {} new events",
            newEvents.size());

        // Create RSS document with new events
        final DocumentBuilder builder
            = securityConfigurer
            .createSecureDocumentBuilderFactory()
            .newDocumentBuilder();

        final Document doc = builder.newDocument();
        final Element rss = doc.createElement(RSS);
        rss.setAttribute(VERSION_ATTR, RSS_VERSION);
        rss.setAttribute(XMLNS_EV_ATTR, EVENT_NAMESPACE_URI);
        doc.appendChild(rss);

        final Element channel = doc.createElement(CHANNEL);
        rss.appendChild(channel);

        addChannelMetadata(doc, channel);

        // Add new events (sorted by eventDateStart descending), skipping any past retention
        final List<EventItem> sortedEvents = new ArrayList<>(newEvents);
        sortedEvents.sort(Comparator.comparing(EventItem::eventDateStart).reversed());

        // Add new events
        for (final EventItem event : sortedEvents) {
            if (eventFilter.shouldKeep(event)) {
                final Element item = createItemElement(doc, event);
                channel.appendChild(item);
            }
        }

        // Import existing events from old feed (preserving pubDate)
        importExistingEvents(doc, channel, new File(existingFilePath));

        writeRssToFile(doc, filePath);

        LOG.info("RSS feed generated successfully at: {}", filePath);
    }

    /**
     * Imports items from the existing feed, dropping those past the retention period.
     *
     * <p>Errors are logged rather than thrown so a scheduled run still publishes new events.
     *
     * @param doc              the new feed document
     * @param channel          the channel to append items to
     * @param existingFeedFile the existing feed file (may not exist)
     */
    private void importExistingEvents(final Document doc, final Element channel,
                                      final File existingFeedFile) {
        if (!existingFeedFile.exists()) {
            return;
        }
        try {
            final DocumentBuilder builder =
                securityConfigurer.createSecureDocumentBuilderFactory().newDocumentBuilder();
            final NodeList items = builder.parse(existingFeedFile).getElementsByTagName(ITEM);
            int imported = 0;
            for (int i = 0; i < items.getLength(); i++) {
                final Element item = (Element) items.item(i);
                if (eventFilter.shouldKeep(item)) {
                    final Node importedNode = doc.importNode(item, true);
                    removeWhitespaceNodes(importedNode);
                    channel.appendChild(importedNode);
                    imported++;
                }
            }
            LOG.info("Imported {} existing events, dropped {} past retention",
                imported, items.getLength() - imported);
        } catch (final Exception e) {
            LOG.error("Failed to import existing events from {}: {}",
                existingFeedFile, e.getMessage(), e);
        }
    }

    @Override
    public Set<String> loadExistingGuids(final String filePath)
        throws Exception {
        requireNonNull(filePath, "filePath must not be null");
        final Set<String> guids = new HashSet<>();
        final File file = new File(filePath);

        if (!file.exists()) {
            LOG.info("No existing feed file found at: {}", filePath);
            return guids;
        }

        final DocumentBuilder builder
            = securityConfigurer
            .createSecureDocumentBuilderFactory()
            .newDocumentBuilder();

        final Document doc = builder.parse(file);
        final NodeList items = doc.getElementsByTagName(ITEM);

        for (int i = 0; i < items.getLength(); i++) {
            final Element item = (Element) items.item(i);
            final NodeList guidNodes = item.getElementsByTagName(GUID);
            if (guidNodes.getLength() > 0) {
                final String guid = guidNodes.item(0).getTextContent();
                guids.add(guid);
            }
        }

        LOG.info("Loaded {} existing GUIDs from feed", guids.size());
        return guids;
    }

    /**
     * Removes whitespace-only text nodes from a DOM tree.
     *
     * <p>This is necessary to ensure proper indentation when writing XML.
     *
     * @param node The root node to clean
     */
    private void removeWhitespaceNodes(final Node node) {
        final Deque<Node> stack = new ArrayDeque<>();
        stack.push(node);

        while (!stack.isEmpty()) {
            final Node current = stack.pop();
            final NodeList children = current.getChildNodes();

            for (int i = children.getLength() - 1; i >= 0; i--) {
                final Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    if (child.getTextContent().trim().isEmpty()) {
                        current.removeChild(child);
                    }
                } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                    stack.push(child);
                }
            }
        }
    }

    private void writeRssToFile(final Document doc, final String filePath)
        throws TransformerException, IOException {
        final Transformer transformer
            = securityConfigurer
            .createSecureTransformerFactory()
            .newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, ENCODING_UTF8);
        transformer.setOutputProperty(XSLT_INDENT_PROPERTY, INDENT_AMOUNT);

        final DOMSource source = new DOMSource(doc);

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            final StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
        }
    }
}
