package improv.raleigh.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageExtractorTest {

    private ImageExtractor extractor;

    @Test
    void extractImageUrl_withAbsoluteUrl_returnsFullUrl() {
        String html = "<div>"
            + "<div class=\"img bgimage\" style=\"background-image: url('https://example"
            + ".com/image.jpg')\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    void extractImageUrl_withBothStyleAndDataSrc_prefersStyle() {
        String html = "<div>"
            + "<div class=\"img bgimage\" style=\"background-image: url('/images/eager.jpg')\" "
            + "data-src=\"/images/lazy.jpg\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("/images/eager.jpg");
    }

    @Test
    void extractImageUrl_withComplexStyleAttribute_extractsUrl() {
        String html = "<div>"
            + "<div class=\"img bgimage\" style=\"width: 100%; height: 200px; background-image: "
            + "url('/images/complex.jpg'); opacity: 0.8;\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("/images/complex.jpg");
    }

    @Test
    void extractImageUrl_withDataSrc_returnsDataSrcUrl() {
        String html = "<div>"
            + "<div class=\"img bgimage\" data-src=\"/images/lazy-load.jpg\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("/images/lazy-load.jpg");
    }

    @Test
    void extractImageUrl_withEmptyImgDiv_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"img bgimage\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extractImageUrl_withEmptyStyle_fallsBackToDataSrc() {
        String html = "<div>"
            + "<div class=\"img bgimage\" style=\"\" data-src=\"/images/fallback.jpg\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("/images/fallback.jpg");
    }

    @Test
    void extractImageUrl_withNoImgDiv_returnsEmptyString() {
        String html = "<div><p>No image</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extractImageUrl_withStyleBackgroundImageDoubleQuotes_returnsUrl() {
        String html = "<div>"
            + "<div class=\"img bgimage\" style='background-image: url(\"/images/show.jpg\")"
            + "'></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("/images/show.jpg");
    }

    @Test
    void extractImageUrl_withStyleBackgroundImageNoQuotes_returnsUrl() {
        String html = "<div>"
            + "<div class=\"img bgimage\" style=\"background-image: url(/images/show.jpg)\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("/images/show.jpg");
    }

    @Test
    void extractImageUrl_withStyleBackgroundImage_returnsUrl() {
        String html = "<div>"
            + "<div class=\"img bgimage\" style=\"background-image: url('/images/show.jpg')"
            + "\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("/images/show.jpg");
    }

    @BeforeEach
    void setUp() {
        extractor = new ImageExtractor();
    }
}
