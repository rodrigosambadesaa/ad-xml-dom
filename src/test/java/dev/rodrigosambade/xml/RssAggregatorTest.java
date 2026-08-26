package dev.rodrigosambade.xml;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RssAggregatorTest{@Test void sortsNewestFirst()throws Exception{try(var in=getClass().getResourceAsStream("/sample.xml")){var n=RssAggregator.parse(in);assertEquals("Segunda",n.getFirst().title());}}}
