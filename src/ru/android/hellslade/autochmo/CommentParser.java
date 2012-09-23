package ru.android.hellslade.autochmo;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CommentParser extends CommentBaseParser {
	protected CommentParser(String xmlString){
        super(xmlString);
    }
    
    public List<Comment> parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            CommentHandler handler = new CommentHandler();
            parser.parse(this.getInputStream(), handler);
            return handler.getComments();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
