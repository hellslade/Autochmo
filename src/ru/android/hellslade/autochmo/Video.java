package ru.android.hellslade.autochmo;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable{
	private String _url;
	private String _original;
	private String _medium;
	private String _small;
	private String _tiny;
	
	public Video() {
	}
	public Video(Parcel in) {
    	this._url = in.readString();
    	this._original = in.readString();
    	this._medium = in.readString();
    	this._small = in.readString();
    	this._tiny = in.readString();
	}
	public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_url);
        parcel.writeString(_original);
        parcel.writeString(_medium);
        parcel.writeString(_small);
        parcel.writeString(_tiny);
	}
	public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
   	 
        public Video createFromParcel(Parcel in) {
          return new Video(in);
        }
     
        public Video[] newArray(int size) {
          return new Video[size];
        }
    };
    public int describeContents() {
        return 0;
    }
    
	public String getUrl() {
		return _url;
	}
	public String getOriginal() {
		return _original;
	}
	public String getMedium() {
		return _medium;
	}
	public String getSmall() {
		return _small;
	}
	public String getTiny() {
		return _tiny;
	}
	public void setUrl(String url) {
		this._url = url;
	}
	public void setOriginal(String original) {
		this._original = original;
	}
	public void setMedium(String medium) {
		this._medium = medium;
	}
	public void setSmall(String small) {
		this._small = small;
	}
	public void setTiny(String tiny) {
		this._tiny = tiny;	}
}
