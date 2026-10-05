package improv.raleigh.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DescriptionExtractorTest {

    private DescriptionExtractor extractor;

    @Test
    void extract_withComplexTimeFormat_returnsFullText() {
        String html = "<div>"
            + "<div class=\"times\">Friday: 7:00 PM, 9:30 PM | Saturday: 7:00 PM, 9:30 PM</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Friday: 7:00 PM, 9:30 PM | Saturday: 7:00 PM, 9:30 PM");
    }

    @Test
    void extract_withEmptyTimesDiv_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"times\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withNoTimesDiv_returnsEmptyString() {
        String html = "<div><p>Some content</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withSingleTime_returnsTime() {
        String html = "<div>"
            + "<div class=\"times\">8:00 PM</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("8:00 PM");
    }

    @Test
    void extract_withTimesDiv_returnsTimesText() {
        String html = "<div>"
            + "<div class=\"times\">7:00 PM & 9:30 PM</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("7:00 PM & 9:30 PM");
    }

    @Test
    void extract_withWhitespace_returnsTrimmedText() {
        String html = "<div>"
            + "<div class=\"times\">  7:30 PM  </div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("7:30 PM");
    }

    @BeforeEach
    void setUp() {
        extractor = new DescriptionExtractor();
    }
}
