package ru.android.hellslade.autochmo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CarmodelBaseParser {
    // Имена тегов XML
    static final String MODEL = "model";
    static final String MODELID = "id";
    static final String MANUFACTURERID = "manufacturerid";
    static final String MANUFACTURERNAME = "manufacturername";
    
    final String xmlString;
    
    protected CarmodelBaseParser(String xmlString){
        this.xmlString = xmlString;
    }
     
    protected InputStream getInputStream() {
        return new ByteArrayInputStream(xmlString.getBytes());
    }
}
