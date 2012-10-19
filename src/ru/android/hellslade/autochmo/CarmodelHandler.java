package ru.android.hellslade.autochmo;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import static ru.android.hellslade.autochmo.CarmodelBaseParser.*;

public class CarmodelHandler extends DefaultHandler {

    private List<Carmodel> carmodels;
    private Carmodel currentCarmodel;
    private StringBuilder builder;
    
    public List<Carmodel> getCarmodels(){
        return this.carmodels;
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
        if (this.currentCarmodel != null){
            if (localName.equalsIgnoreCase(MODEL)){
                currentCarmodel.setModelName(builder.toString().trim());
                carmodels.add(currentCarmodel);
            }
        }
        builder.setLength(0);    
    }
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        carmodels = new ArrayList<Carmodel>();
        builder = new StringBuilder();
        //Log.v("StartDocument");
    }
 
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
//        Log.v("startElement " + localName);
        if (localName.equalsIgnoreCase(MODEL)){
            this.currentCarmodel = new Carmodel();
        }
        if (this.currentCarmodel != null){
            if (localName.equalsIgnoreCase(MODEL)){
                currentCarmodel.setModelId(attributes.getValue(0).toString());
                currentCarmodel.setManufacturerId(attributes.getValue(1).toString());
                currentCarmodel.setManufacturerName(attributes.getValue(2).toString());
            }
        }
    }
}