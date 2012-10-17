package ru.android.hellslade.autochmo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public abstract class FactBaseParser implements FactXMLParser {
	 
	// Имена тегов XML
	static final String FACT = "fact";
	static final String NAME = "name";
	static final String COMMENT = "comment";
	static final String DATECREATED = "datecreated";
	static final String GOSNOMER = "gosnomer";
	static final String LATITUDE = "latitude";
	static final String LONGITUDE = "longitude";
	static final String CARMODEL = "carmodel";
	static final String RATING = "rating";
	static final String COMMENTSNUM = "commentsnum";
	static final String COUNTS = "counts";
	static final String VIDEOS = "videos";
	static final String VIDEO = "video";
	static final String VIDEOURL = "url";
	static final String ORIGINAL = "original";
	static final String MEDIUM = "medium";
	static final String SMALL = "small";
	static final String TINY = "tiny";
	static final String SRC = "src";
	 
	final String xmlString;
	 
	protected FactBaseParser(String xmlString){
		this.xmlString = xmlString;
	}
	 
	protected InputStream getInputStream() {
		return new ByteArrayInputStream(xmlString.getBytes());
	}
}