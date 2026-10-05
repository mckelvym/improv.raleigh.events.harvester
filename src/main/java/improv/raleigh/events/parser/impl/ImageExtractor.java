package improv.raleigh.events.parser.impl;

import static improv.raleigh.events.parser.impl.CssSelectors.IMAGE_BG;
import static improv.raleigh.events.parser.impl.HtmlConstants.DATA_SRC_ATTR;
import static improv.raleigh.events.parser.impl.HtmlConstants.EMPTY;
import static improv.raleigh.events.parser.impl.HtmlConstants.STYLE_ATTR;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;

/**
 * Extracts event image URL from HTML.
 */
public class ImageExtractor {
    private static final Pattern BACKGROUND_IMAGE_PATTERN
        = Pattern.compile("background-image:\\s*url\\(['\"]?([^'\")]+)['\"]?\\)");

    /**
     * Extracts the image URL from an event element.
     *
     * @param element the event element
     * @return the extracted image URL, or empty string if not found
     */
    public String extractImageUrl(final Element element) {
        final Element imgDiv = element.selectFirst(IMAGE_BG);
        if (imgDiv != null) {
            // First try to extract from style attribute (for eagerly loaded images)
            final String style = imgDiv.attr(STYLE_ATTR);
            if (!style.isEmpty()) {
                final Matcher matcher = BACKGROUND_IMAGE_PATTERN.matcher(style);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }

            // If not found in style, try data-src (for lazy loaded images)
            final String dataSrc = imgDiv.attr(DATA_SRC_ATTR);
            if (!dataSrc.isEmpty()) {
                return dataSrc;
            }
        }

        return EMPTY;
    }
}
