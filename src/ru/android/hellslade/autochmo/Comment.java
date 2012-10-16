package ru.android.hellslade.autochmo;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment  implements Comparable<Comment>, Parcelable {
	
    private String _userpicture;
	private String _commentId;
	private String _text;
	private String _username;
	private String _usersecondname;
	private String _userlastname;
	private String _userlogin;
	private String _datecreated;
	private String _datecreatedStr;
	private String _entity;
	private String _entityType;
	
    public Comment(){
    }
    public Comment(Parcel p) {
    	this._commentId = p.readString();
    	this._text = p.readString();
    	this._username = p.readString();
    	this._userpicture = p.readString();
    	this._usersecondname = p.readString();
    	this._userlastname = p.readString();
    	this._userlogin = p.readString();
    	this._datecreated = p.readString();
    	this._datecreatedStr = p.readString();
    	this._entity = p.readString();
    	this._entityType = p.readString();
    }
    public void setUserPicture(String url) {
        this._userpicture = url;
    }
    public String getUserPicture() {
//    	return "/bitrix/templates/avtochmo/images/userpic_50.png";
    	return this._userpicture;
    }
    public void setCommentId(String commentId) {
    	this._commentId = commentId;
    }
    public String getCommentId() {
    	return this._commentId;
    }
    public void setText(String text) {
    	this._text = text;
    }
    public String getText() {
    	return this._text;
    }
    public void setUsername(String username) {
    	this._username = username;
    }
    public String getUsername() {
    	return this._username;
    }
    public String getUserFullname() {
        return String.format("%s %s %s", this._username, this._usersecondname, this._userlastname);
    }
    public void setUsersecondname(String usersecondname) {
    	this._usersecondname = usersecondname;
    }
    public String getUsersecondname() {
    	return this._usersecondname;
    }
    public void setUserlastname(String userlastname) {
    	this._userlastname = userlastname;
    }
    public String getUserlastname() {
    	return this._userlastname;
    }
    public void setUserlogin(String userlogin) {
    	this._userlogin = userlogin;
    }
    public String getUserlogin() {
    	return this._userlogin;
    }
    public void setDatecreated(String dat) {
    	this._datecreated = dat;
    }
    public String getDatecreated() {
    	return this._datecreated;
    }
    public void setDatecreatedStr(String dat) {
    	this._datecreatedStr = dat;
    }
    public String getDatecreatedStr() {
    	return this._datecreatedStr;
    }
    public void setEntity(String entity) {
    	this._entity = entity;
    }
    public String getEntity() {
    	return this._entity;
    }
    public void setEntityType(String entityType) {
    	this._entityType = entityType;
    }
    public String getEntityType() {
    	return this._entityType;
    }
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(_commentId);
		parcel.writeString(_text);
		parcel.writeString(_username);
		parcel.writeString(_userpicture);
		parcel.writeString(_usersecondname);
		parcel.writeString(_userlastname);
		parcel.writeString(_userlogin);
		parcel.writeString(_datecreated);
		parcel.writeString(_datecreatedStr);
		parcel.writeString(_entity);
		parcel.writeString(_entityType);
	}
    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
   	 
        public Comment createFromParcel(Parcel in) {
          return new Comment(in);
        }
     
        public Comment[] newArray(int size) {
          return new Comment[size];
        }
      };

	public int compareTo(Comment another) {
		if (another == null) return 1;
        // сортировка по убывания, наиболее свежие записи выводят сверху
        return another._datecreated.compareTo(this.getDatecreated());
	}
}
