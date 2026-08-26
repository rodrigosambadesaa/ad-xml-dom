package dev.rodrigosambade.xml;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RssAggregatorTest {
    @Test
    void sortsNewestFirst() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/sample.xml")) {
            assertNotNull(input);

            List<NewsItem> items = RssAggregator.parse(input);

            assertEquals("Segunda", items.getFirst().title());
        }
    }
}
