package dev.rodrigosambade.xml;
import java.time.*;
public record NewsItem(String title,String description,String link,ZonedDateTime published) implements Comparable<NewsItem>{@Override public int compareTo(NewsItem o){return o.published.compareTo(published);}}
