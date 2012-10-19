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
        try {
            SAXParser parser = factory.newSAXParser();
            
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
