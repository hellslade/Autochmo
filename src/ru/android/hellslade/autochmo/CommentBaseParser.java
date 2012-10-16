package ru.android.hellslade.autochmo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public abstract class CommentBaseParser implements CommentXMLParser{

	// Имена тегов XML
	static final String COMMENT = "comment";
	static final String TEXT = "text";
	static final String USERNAME = "username";
	static final String USERPICTURE = "userpic";
	static final String USERSECONDNAME = "usersecondname";
	static final String USERLASTNAME = "userlastname";
	static final String USERLOGIN = "userlogin";
	static final String DATECREATED = "datecreated";
	static final String ENTITY = "entity";
	static final String ENTITYTYPE = "entitytype";
	 
	final String xmlString;
	 
	protected CommentBaseParser(String xmlString){
		this.xmlString = xmlString;
	}
	 
	protected InputStream getInputStream() {
		return new ByteArrayInputStream(xmlString.getBytes());
	}
}
