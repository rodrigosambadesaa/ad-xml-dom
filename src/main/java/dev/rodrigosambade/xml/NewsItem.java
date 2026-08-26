package dev.rodrigosambade.xml;

import java.time.ZonedDateTime;

public record NewsItem(
        String title,
        String description,
        String link,
        ZonedDateTime published) implements Comparable<NewsItem> {

    @Override
    public int compareTo(NewsItem other) {
        return other.published.compareTo(published);
    }
}
