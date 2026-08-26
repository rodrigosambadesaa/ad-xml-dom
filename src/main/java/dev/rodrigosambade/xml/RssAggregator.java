package dev.rodrigosambade.xml;
import org.w3c.dom.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
public final class RssAggregator {
 static DocumentBuilder builder()throws Exception{DocumentBuilderFactory f=DocumentBuilderFactory.newInstance();f.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);f.setFeature("http://xml.org/sax/features/external-general-entities",false);f.setFeature("http://xml.org/sax/features/external-parameter-entities",false);f.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD,"");f.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA,"");return f.newDocumentBuilder();}
 public static List<NewsItem> parse(InputStream in)throws Exception{Document d=builder().parse(in);NodeList items=d.getElementsByTagName("item");List<NewsItem>x=new ArrayList<>();for(int i=0;i<items.getLength();i++){Element e=(Element)items.item(i);x.add(new NewsItem(text(e,"title"),text(e,"description"),text(e,"link"),parseDate(text(e,"pubDate"))));}Collections.sort(x);return List.copyOf(x);}
 static String text(Element e,String tag){NodeList n=e.getElementsByTagName(tag);return n.getLength()==0?"":n.item(0).getTextContent().trim();}
 static ZonedDateTime parseDate(String s){try{return ZonedDateTime.parse(s,DateTimeFormatter.RFC_1123_DATE_TIME);}catch(Exception e){return ZonedDateTime.ofInstant(Instant.EPOCH,ZoneOffset.UTC);}}
 public static void writeHtml(List<NewsItem> items,Path p)throws IOException{StringBuilder b=new StringBuilder("<!doctype html><html lang=\"es\"><meta charset=\"utf-8\"><title>Mis Noticias</title><body>");for(var n:items)b.append("<article><h2>").append(esc(n.title())).append("</h2><p>").append(esc(n.description())).append("</p><a href=\"").append(esc(n.link())).append("\">Fuente</a><time>").append(n.published()).append("</time></article>");b.append("</body></html>");Files.writeString(p,b,StandardCharsets.UTF_8);}
 public static void writeRss(List<NewsItem> items,Path p)throws Exception{Document d=builder().newDocument();Element rss=d.createElement("rss");rss.setAttribute("version","2.0");d.appendChild(rss);Element ch=d.createElement("channel");rss.appendChild(ch);add(d,ch,"title","Mis Noticias");for(var n:items){Element item=d.createElement("item");ch.appendChild(item);add(d,item,"title",n.title());add(d,item,"description",n.description());add(d,item,"link",n.link());add(d,item,"pubDate",n.published().format(DateTimeFormatter.RFC_1123_DATE_TIME));}TransformerFactory tf=TransformerFactory.newInstance();tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD,"");tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET,"");Transformer t=tf.newTransformer();t.setOutputProperty(OutputKeys.INDENT,"yes");t.transform(new DOMSource(d),new StreamResult(p.toFile()));}
 static void add(Document d,Element p,String n,String v){Element e=d.createElement(n);e.setTextContent(v);p.appendChild(e);}static String esc(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");}
 public static void main(String[]a)throws Exception{if(a.length!=3)throw new IllegalArgumentException("Uso: input.xml output.html output.xml");List<NewsItem> n;try(var in=Files.newInputStream(Path.of(a[0]))){n=parse(in);}writeHtml(n,Path.of(a[1]));writeRss(n,Path.of(a[2]));}
}
