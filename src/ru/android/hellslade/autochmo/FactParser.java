package ru.android.hellslade.autochmo;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

// see http://www.androiddb.ru/content/rabota-s-xml-v-android-chast-1

public class FactParser extends FactBaseParser {
	protected FactParser(String xmlString){
        super(xmlString);
    }
    
    public List<Fact> parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            FactHandler handler = new FactHandler();
            parser.parse(this.getInputStream(), handler);
            return handler.getFacts();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
