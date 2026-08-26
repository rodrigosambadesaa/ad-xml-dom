package dev.rodrigosambade.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RssAggregator {
    private static final String DISALLOW_DOCTYPE =
            "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String EXTERNAL_GENERAL_ENTITIES =
            "http://xml.org/sax/features/external-general-entities";
    private static final String EXTERNAL_PARAMETER_ENTITIES =
            "http://xml.org/sax/features/external-parameter-entities";

    private RssAggregator() {
    }

    static DocumentBuilder builder() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(DISALLOW_DOCTYPE, true);
        factory.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
        factory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return factory.newDocumentBuilder();
    }

    public static List<NewsItem> parse(InputStream input) throws Exception {
        Document document = builder().parse(input);
        NodeList itemNodes = document.getElementsByTagName("item");
        List<NewsItem> items = new ArrayList<>(itemNodes.getLength());

        for (int index = 0; index < itemNodes.getLength(); index++) {
            Element itemElement = (Element) itemNodes.item(index);
            items.add(toNewsItem(itemElement));
        }

        Collections.sort(items);
        return List.copyOf(items);
    }

    private static NewsItem toNewsItem(Element itemElement) {
        return new NewsItem(
                text(itemElement, "title"),
                text(itemElement, "description"),
                text(itemElement, "link"),
                parseDate(text(itemElement, "pubDate")));
    }

    static String text(Element element, String tag) {
        NodeList nodes = element.getElementsByTagName(tag);
        if (nodes.getLength() == 0) {
            return "";
        }
        return nodes.item(0).getTextContent().trim();
    }

    static ZonedDateTime parseDate(String value) {
        try {
            return ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
        } catch (DateTimeParseException exception) {
            return ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
        }
    }

    public static void writeHtml(List<NewsItem> items, Path target) throws IOException {
        StringBuilder html = new StringBuilder(
                "<!doctype html><html lang=\"es\"><meta charset=\"utf-8\">"
                        + "<title>Mis Noticias</title><body>");

        for (NewsItem item : items) {
            appendHtmlItem(html, item);
        }

        html.append("</body></html>");
        Files.writeString(target, html, StandardCharsets.UTF_8);
    }

    private static void appendHtmlItem(StringBuilder html, NewsItem item) {
        html.append("<article><h2>")
                .append(escapeHtml(item.title()))
                .append("</h2><p>")
                .append(escapeHtml(item.description()))
                .append("</p><a href=\"")
                .append(escapeHtml(item.link()))
                .append("\">Fuente</a><time>")
                .append(item.published())
                .append("</time></article>");
    }

    public static void writeRss(List<NewsItem> items, Path target) throws Exception {
        Document document = createRssDocument(items);
        Transformer transformer = secureTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(target.toFile()));
    }

    private static Document createRssDocument(List<NewsItem> items) throws Exception {
        Document document = builder().newDocument();

        Element rss = document.createElement("rss");
        rss.setAttribute("version", "2.0");
        document.appendChild(rss);

        Element channel = document.createElement("channel");
        rss.appendChild(channel);
        add(document, channel, "title", "Mis Noticias");

        for (NewsItem item : items) {
            appendRssItem(document, channel, item);
        }

        return document;
    }

    private static void appendRssItem(Document document, Element channel, NewsItem item) {
        Element itemElement = document.createElement("item");
        channel.appendChild(itemElement);

        add(document, itemElement, "title", item.title());
        add(document, itemElement, "description", item.description());
        add(document, itemElement, "link", item.link());
        add(
                document,
                itemElement,
                "pubDate",
                item.published().format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    private static Transformer secureTransformer() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
        return transformer;
    }

    static void add(Document document, Element parent, String name, String value) {
        Element child = document.createElement(name);
        child.setTextContent(value);
        parent.appendChild(child);
    }

    static String esc(String value) {
        return escapeHtml(value);
    }

    private static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("Uso: input.xml output.html output.xml");
        }

        Path input = Path.of(args[0]);
        Path htmlOutput = Path.of(args[1]);
        Path rssOutput = Path.of(args[2]);

        List<NewsItem> items;
        try (InputStream stream = Files.newInputStream(input)) {
            items = parse(stream);
        }

        writeHtml(items, htmlOutput);
        writeRss(items, rssOutput);
    }
}
