package ru.android.hellslade.autochmo;

import static ru.android.hellslade.autochmo.FactBaseParser.*;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FactHandler extends DefaultHandler{
    private List<Fact> facts;
    private Fact currentFact;
    private StringBuilder builder;
    private boolean counts;
    private boolean original;
    private boolean medium;
    private boolean small;
    private boolean tiny;
    
    public List<Fact> getFacts(){
        return this.facts;
    }
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }
 
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        super.endElement(uri, localName, name);
        if (this.currentFact != null){
            if (localName.equalsIgnoreCase(NAME)){
            	currentFact.setName(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(COMMENT)){
            	currentFact.setComment(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(DATECREATED)){
            	currentFact.setDatecreated(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(GOSNOMER)){
            	if (counts){
            		currentFact.setCountsGosnomer(builder.toString().trim());
            	} else {
            		currentFact.setGosnomer(builder.toString().trim());
            	}
            } else if (localName.equalsIgnoreCase(LATITUDE)){
            	currentFact.setLatitude(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(LONGITUDE)){
            	currentFact.setLongitude(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(CARMODEL)){
            	if (counts){
            		currentFact.setCountsCarmodel(builder.toString().trim());
            	} else {
            		currentFact.setCarmodel(builder.toString().trim());
            	}
            } else if (localName.equalsIgnoreCase(RATING)){
            	currentFact.setRating(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(COMMENTSNUM)){
            	currentFact.setCommentsnum(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(COUNTS)){
            	counts = false;
            } else if (localName.equalsIgnoreCase(ORIGINAL)){
            	original = false;
            } else if (localName.equalsIgnoreCase(MEDIUM)){
            	medium = false;
            } else if (localName.equalsIgnoreCase(SMALL)){
            	small = false;
            } else if (localName.equalsIgnoreCase(TINY)){
            	tiny = false;
            } else if (localName.equalsIgnoreCase(SRC)){
            	if (original){
            		currentFact.setPictureOriginal(builder.toString().trim());
            	} else if (medium){
        			currentFact.setPictureMedium(builder.toString().trim());
            	} else if (small){
        			currentFact.setPictureSmall(builder.toString().trim());
            	} else if (tiny){
        			currentFact.setPictureTiny(builder.toString().trim());
            	}
            } else if (localName.equalsIgnoreCase(FACT)){
                facts.add(currentFact);
            }
        }
        builder.setLength(0);    
    }
    
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        facts = new ArrayList<Fact>();
        builder = new StringBuilder();
        //Log.v("StartDocument");
    }
 
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
//        Log.v("startElement " + localName);
        if (localName.equalsIgnoreCase(FACT)){
            this.currentFact = new Fact();
        }
        if (this.currentFact != null){
        	if (localName.equalsIgnoreCase(FACT)){
        		currentFact.setFactId(attributes.getValue(0).toString());
        	} else if (localName.equalsIgnoreCase(DATECREATED)){
        		currentFact.setDatecreatedStr(attributes.getValue(0).toString());
        	} else if (localName.equalsIgnoreCase(GOSNOMER) && !counts){
        		currentFact.setGosnomerId(attributes.getValue(0).toString());
        		currentFact.setGosnomerType(attributes.getValue(1).toString());
        		currentFact.setGosnomerNomer(attributes.getValue(2).toString());
        		currentFact.setGosnomerNumber(attributes.getValue(3).toString());
        		currentFact.setGosnomerSeries(attributes.getValue(4).toString());
        		currentFact.setGosnomerRegion(attributes.getValue(5).toString());
        	} else if (localName.equalsIgnoreCase(CARMODEL) && !counts){
        		currentFact.setCarmodelId(attributes.getValue(0).toString());
        		currentFact.setCarmodelManufacturer(attributes.getValue(1).toString());
        	} else if (localName.equalsIgnoreCase(RATING)){
        		currentFact.setRatingPlus(attributes.getValue(0).toString());
        		currentFact.setRatingMinus(attributes.getValue(1).toString());
        	} else if (localName.equalsIgnoreCase(COUNTS)){
        		counts = true;
        	} else if (localName.equalsIgnoreCase(ORIGINAL)){
        		original = true;
        	} else if (localName.equalsIgnoreCase(MEDIUM)){
        		medium = true;
        	} else if (localName.equalsIgnoreCase(SMALL)){
        		small = true;
        	} else if (localName.equalsIgnoreCase(TINY)){
        		tiny = true;
        	}
        }
    }
}