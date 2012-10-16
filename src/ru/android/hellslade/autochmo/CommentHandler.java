package ru.android.hellslade.autochmo;

import static ru.android.hellslade.autochmo.CommentBaseParser.*;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CommentHandler extends DefaultHandler {
    private List<Comment> comments;
    private Comment       currentComment;
    private StringBuilder builder;

    public List<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        super.endElement(uri, localName, name);
        if (this.currentComment != null) {
            if (localName.equalsIgnoreCase(TEXT)) {
                currentComment.setText(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(USERNAME)) {
                currentComment.setUsername(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(USERPICTURE)) {
            	Log.v("userpic " + builder.toString().trim());
                currentComment.setUserPicture(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(USERSECONDNAME)) {
                currentComment.setUsersecondname(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(USERLASTNAME)) {
                currentComment.setUserlastname(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(USERLOGIN)) {
                currentComment.setUserlogin(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(DATECREATED)) {
                currentComment.setDatecreated(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(ENTITY)) {
                currentComment.setEntity(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(ENTITYTYPE)) {
                currentComment.setEntityType(builder.toString().trim());
            } else if (localName.equalsIgnoreCase(COMMENT)) {
                comments.add(currentComment);
            }
        }
        builder.setLength(0);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        comments = new ArrayList<Comment>();
        builder = new StringBuilder();
        // Log.v("StartDocument");
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        // Log.v("startElement " + localName);
        if (localName.equalsIgnoreCase(COMMENT)) {
            this.currentComment = new Comment();
        }
        if (this.currentComment != null) {
            if (localName.equalsIgnoreCase(COMMENT)) {
                currentComment.setCommentId(attributes.getValue(0).toString());
            } else if (localName.equalsIgnoreCase(DATECREATED)) {
                currentComment.setDatecreatedStr(attributes.getValue(0)
                        .toString());
            }
        }
    }
}
