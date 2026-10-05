package improv.raleigh.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TitleExtractorTest {

    private TitleExtractor extractor;

    @Test
    void extractTitle_withBlankH3_returnsEmptyString() {
        String html = "<div>"
            + "<h3>   </h3>"
            + "<h2>Other Title</h2>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extractTitle_withBlankHeadings_returnsEmptyString() {
        String html = "<div>"
            + "<h1>   </h1>"
            + "<h2></h2>"
            + "<h3>   </h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extractTitle_withComplexHTML_returnsH3Title() {
        String html = "<div>"
            + "<div class=\"event-details\">"
            + "<h2>Secondary Title</h2>"
            + "<h3>Main Show Title</h3>"
            + "<h4>Subtitle</h4>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEqualTo("Main Show Title");
    }

    @Test
    void extractTitle_withEmptyH3_returnsEmptyString() {
        String html = "<div>"
            + "<h3></h3>"
            + "<h1>Show Title</h1>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extractTitle_withH2AndH3_prioritizesH3() {
        String html = "<div>"
            + "<h2>H2 Title</h2>"
            + "<h3>H3 Title</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEqualTo("H3 Title");
    }

    @Test
    void extractTitle_withH3Element_returnsTitle() {
        String html = "<div><h3>Comedy Show</h3></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEqualTo("Comedy Show");
    }

    @Test
    void extractTitle_withH3Priority_ignoresOtherHeadings() {
        String html = "<div>"
            + "<h1>H1 Title</h1>"
            + "<h3>H3 Title</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEqualTo("H3 Title");
    }

    @Test
    void extractTitle_withH3Whitespace_returnsTrimmedTitle() {
        String html = "<div><h3>  Funny Night  </h3></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEqualTo("Funny Night");
    }

    @Test
    void extractTitle_withH4AndH5_returnsFirstMatch() {
        String html = "<div>"
            + "<h4>H4 Title</h4>"
            + "<h5>H5 Title</h5>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEqualTo("H4 Title");
    }

    @Test
    void extractTitle_withH6_returnsTitle() {
        String html = "<div><h6>Event Title</h6></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEqualTo("Event Title");
    }

    @Test
    void extractTitle_withMultipleHeadings_prioritizesH3() {
        String html = "<div>"
            + "<h1>First Show</h1>"
            + "<h2>Second Show</h2>"
            + "<h3>Third Show</h3>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEqualTo("Third Show");
    }

    @Test
    void extractTitle_withNestedElements_returnsText() {
        String html = "<div><h3>Event <span>Name</span></h3></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEqualTo("Event Name");
    }

    @Test
    void extractTitle_withNoH3_usesHeadingTags() {
        String html = "<div><h2>Main Event</h2></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEqualTo("Main Event");
    }

    @Test
    void extractTitle_withNoHeadings_returnsEmptyString() {
        String html = "<div><p>Some content</p><span>More content</span></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractTitle(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extractTitle_withNullElement_throwsNullPointerException() {
        try {
            extractor.extractTitle(null);
            // If we get here, the test should fail
            assertThat(true).isFalse();
        } catch (NullPointerException e) {
            // Expected behavior
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }

    @BeforeEach
    void setUp() {
        extractor = new TitleExtractor();
    }
}
