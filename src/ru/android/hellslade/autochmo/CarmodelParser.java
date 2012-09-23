package ru.android.hellslade.autochmo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CarmodelParser extends CarmodelBaseParser {
    protected CarmodelParser(String xmlString){
        super(xmlString);
    }
    
    public List<Carmodel> parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        /*factory.setNamespaceAware(true);
        factory.setValidating(true);*/
        try {
            SAXParser parser = factory.newSAXParser();
            /*parser.setProperty
                ("http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
                        "http://www.w3.org/2001/XMLSchema");*/
            CarmodelHandler handler = new CarmodelHandler();
            parser.parse(this.getInputStream(), handler);
            return handler.getCarmodels();
        } catch (Exception e) {
            Log.v(e.getMessage());
            return new ArrayList<Carmodel>();
            //throw new RuntimeException(e);
        }
    }
}
