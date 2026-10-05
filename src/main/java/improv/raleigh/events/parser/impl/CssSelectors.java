package improv.raleigh.events.parser.impl;

/**
 * Constants for CSS selectors used in HTML parsing.
 *
 * <p>This class centralizes all CSS selector strings used throughout the
 * parser implementation to avoid magic strings and improve maintainability.
 */
public final class CssSelectors {

    /**
     * Selector for event items on the calendar page.
     */
    public static final String EVENT_ITEM = "a.item";

    /**
     * Selector for "More Shows" pagination button.
     */
    public static final String MORE_SHOWS_BUTTON = "#moreshowsbtn";

    /**
     * Selector for h3 heading element.
     */
    public static final String H3 = "h3";

    /**
     * Selector for all heading tags.
     */
    public static final String ALL_HEADINGS = "h1, h2, h3, h4, h5, h6";

    /**
     * Selector for dates container.
     */
    public static final String DATES = "div.dates";

    /**
     * Selector for date term (month).
     */
    public static final String DATE_TERM = "dt";

    /**
     * Selector for date description (day).
     */
    public static final String DATE_DESCRIPTION = "dd";

    /**
     * Selector for day of week element.
     */
    public static final String DAY_OF_WEEK = "i";

    /**
     * Selector for times container.
     */
    public static final String TIMES = "div.times";

    /**
     * Selector for image div with background.
     */
    public static final String IMAGE_BG = "div.img.bgimage";

    /**
     * Selector for bio text on event detail page.
     */
    public static final String BIO_TEXT = "div.bio-text";

    /**
     * Selector for meta description tag.
     */
    public static final String META_DESCRIPTION = "meta[name=description]";

    /**
     * Selector for Open Graph image meta tag.
     */
    public static final String META_OG_IMAGE = "meta[property=og:image]";

    /**
     * Selector for Twitter image meta tag.
     */
    public static final String META_TWITTER_IMAGE = "meta[name=twitter:image]";

    /**
     * Selector for page load wait condition.
     */
    public static final String PAGE_LOAD_SELECTOR = "body";
    static final String CONTENT = "content";

    private CssSelectors() {
        // Utility class - prevent instantiation
    }
}
