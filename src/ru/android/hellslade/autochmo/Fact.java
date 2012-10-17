package ru.android.hellslade.autochmo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;

public class Fact implements Comparable<Fact>, Parcelable {
//    static SimpleDateFormat FORMATTER = 
//        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
    
    private String _fact_id;
    private String _name;
    private String _comment;
    private String _datecreated_str;
    private String _datecreated;
    private String _gosnomer;
    private String _gosnomer_id;
    private String _gosnomer_type;
    private String _gosnomer_nomer;
    private String _gosnomer_number;
    private String _gosnomer_series;
    private String _gosnomer_region;
    private String _latitude;
    private String _longitude;
    private String _carmodel;
    private String _carmodel_id;
    private String _carmodel_manufacturer;
    private String _rating;
    private String _rating_plus;
    private String _rating_minus;
    private String _commentsnum;
    private String _countsgosnomer;
    private String _countscarmodel;
    private String _video_count;
    private List<String> _picture_original = new ArrayList<String>();
    private List<String> _picture_medium = new ArrayList<String>();
    private List<String> _picture_small = new ArrayList<String>();
    private List<String> _picture_tiny = new ArrayList<String>();
    private List<Video> _videos = new ArrayList<Video>();
    
    //private Date date;
    public Fact(){
    }
    public Fact(Parcel in) {
    	this._fact_id = in.readString();
    	this._name = in.readString();
    	this._comment = in.readString();
    	this._datecreated_str = in.readString();
    	this._datecreated = in.readString();
    	this._gosnomer = in.readString();
    	this._gosnomer_id = in.readString();
    	this._gosnomer_type = in.readString();
    	this._gosnomer_nomer = in.readString();
    	this._gosnomer_number = in.readString();
    	this._gosnomer_series = in.readString();
    	this._gosnomer_region = in.readString();
    	this._latitude = in.readString();
    	this._longitude = in.readString();
    	this._carmodel = in.readString();
    	this._carmodel_id = in.readString();
    	this._carmodel_manufacturer = in.readString();
    	this._rating = in.readString();
    	this._rating_plus = in.readString();
    	this._rating_minus = in.readString();
    	this._commentsnum = in.readString();
    	this._countsgosnomer = in.readString();
    	this._countscarmodel = in.readString();
    	this._video_count = in.readString();
    	int count = in.readInt();
    	for (int i = 0; i < count; i++) {
      	  String ph = in.readString();
      	  this._picture_original.add(ph);
      	}
    	for (int i = 0; i < count; i++) {
      	  String ph = in.readString();
      	  this._picture_medium.add(ph);
      	}
    	for (int i = 0; i < count; i++) {
      	  String ph = in.readString();
      	  this._picture_small.add(ph);
      	}
    	for (int i = 0; i < count; i++) {
      	  String ph = in.readString();
      	  this._picture_tiny.add(ph);
      	}
    	for (int i = 0; i < count; i++) {
    	  Video video = in.readParcelable(Video.class.getClassLoader());
    	  this._videos.add(video);
    	}
	}
    public int getPictureCount()
    {
    	return this._picture_original.size();
    }
    public void setFactId(String fact_id)
    {
    	this._fact_id = fact_id;
    }
    public String getFactId()
    {
    	return this._fact_id;
    }
    public void setName(String name)
    {
    	this._name = name;
    }
    public String getName()
    {
    	return this._name;
    }
    public void setComment(String comment)
    {
    	this._comment = comment;
    }
    public String getComment()
    {
    	return this._comment;
    }
    public void setDatecreated(String dat)
    {
			this._datecreated = dat;
    }
    public String getDatecreated()
    {
    	return this._datecreated;
    }
    public void setDatecreatedStr(String dat)
    {
    	this._datecreated_str = dat;
    }
    public String getDatecreatedStr()
    {
    	return this._datecreated_str;
    }
    public void setGosnomer(String nomer)
    {
    	this._gosnomer = nomer;
    }
    public String getGosnomer()
    {
    	return this._gosnomer;
    }
    public void setGosnomerId(String id)
    {
    	this._gosnomer_id = id;
    }
    public String getGosnomerId()
    {
    	return this._gosnomer_id;
    }
    public void setGosnomerType(String type)
    {
    	this._gosnomer_type = type;
    }
    public String getGosnomerType()
    {
    	return this._gosnomer_type;
    }
    /**
     * Буквы и цифры номера без региона
     * 
     * @param nomer
     */
    public void setGosnomerNomer(String nomer)
    {
    	this._gosnomer_nomer = nomer;
    }
    /**
     * Буквы и цифры номера без региона
     * 
     * @return
     */
    public String getGosnomerNomer()
    {
    	return this._gosnomer_nomer;
    }
    /**
     * Цифры номера без букв
     * 
     * @param number
     */
    public void setGosnomerNumber(String number)
    {
    	this._gosnomer_number = number;
    }
    /**
     * Цифры номера без букв
     * 
     * @return
     */
    public String getGosnomerNumber()
    {
    	return this._gosnomer_number;
    }
    public void setGosnomerSeries(String series)
    {
    	this._gosnomer_series = series;
    }
    public String getGosnomerSeries()
    {
    	return this._gosnomer_series;
    }
    public void setGosnomerRegion(String region)
    {
    	this._gosnomer_region = region;
    }
    public String getGosnomerRegion()
    {
    	return this._gosnomer_region;
    }
    /**
     * Широта
     * 
     * @return
     */
    public void setLatitude(String latitude)
    {
    	this._latitude = latitude;
    }
    public String getLatitude()
    {
    	return this._latitude;
    }
    /**
     * Долгота
     * 
     * @return
     */
    public void setLongitude(String longitude)
    {
        this._longitude = longitude;
    }
    public String getLongitude()
    {
    	return this._longitude;
    }
    public void setCarmodel(String carmodel)
    {
    	this._carmodel = carmodel;
    }
    public String getCarModel()
    {
    	return this._carmodel;
    }
    public void setCarmodelId(String carmodelid)
    {
    	this._carmodel_id = carmodelid;
    }
    public String getCarModelId()
    {
    	return this._carmodel_id;
    }
    public void setCarmodelManufacturer(String carmodelmanufacturer)
    {
    	this._carmodel_manufacturer = carmodelmanufacturer;
    }
    public String getCarModelManufacturer()
    {
    	return this._carmodel_manufacturer;
    }
    public void setRating(String rating)
    {
    	this._rating = rating;
    }
    public String getRating()
    {
    	return this._rating;
    }
    public void setRatingPlus(String rating_plus)
    {
    	this._rating_plus = rating_plus;
    }
    public String getRatingPlus()
    {
    	return this._rating_plus;
    }
    public void setRatingMinus(String rating_minus)
    {
    	this._rating_minus = rating_minus;
    }
    public String getRatingMinus()
    {
    	return this._rating_minus;
    }
    public void setCommentsnum(String commentsnum)
    {
    	this._commentsnum = commentsnum;
    }
    public String getCommentsnum()
    {
    	return this._commentsnum;
    }
    public void setCountsGosnomer(String count)
    {
    	this._countsgosnomer = count;
    }
    public String getCountsGosnomer()
    {
    	return this._countsgosnomer;
    }
    public void setCountsCarmodel(String count)
    {
    	this._countscarmodel = count;
    }
    public String getCountsCarmodel()
    {
    	return this._countscarmodel;
    }
    public void setVideoCount(String count)
    {
    	this._video_count = count;
    }
    public String getVideoCount()
    {
    	return this._video_count;
    }
    public void setPictureOriginal(String src)
    {
    	this._picture_original.add(src);
    }
    public String getPictureOriginal(int idx)
    {
		try {
			return this._picture_original.get(idx);
		} catch (IndexOutOfBoundsException e) {
			return "";
		}
    }
    public void setPictureMedium(String src)
    {
    	this._picture_medium.add(src);
    }
    public String getPictureMedium(int idx)
    {
		try {
			return this._picture_medium.get(idx);
		} catch (IndexOutOfBoundsException e) {
			return "";
		}
    }
    public void setPictureSmall(String src)
    {
    	this._picture_small.add(src);
    }
    public String getPictureSmall(int idx)
    {
    	try {
			return this._picture_small.get(idx);
		} catch (IndexOutOfBoundsException e) {
			return "";
		}
    }
    public void setPictureTiny(String src)
    {
    	this._picture_tiny.add(src);
    }
    public String getPictureTiny(int idx)
    {
		try {
			return this._picture_tiny.get(idx);
		} catch (IndexOutOfBoundsException e) {
			return "";
		}
    }
    public void addVideo(Video v)
    {
    	this._videos.add(v);
    }
    public Video getVideo(int idx)
    {
		try {
			return this._videos.get(idx);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
    }
    
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_fact_id);
        parcel.writeString(_name);
        parcel.writeString(_comment);
        parcel.writeString(_datecreated_str);
        parcel.writeString(_datecreated);
        parcel.writeString(_gosnomer);
        parcel.writeString(_gosnomer_id);
        parcel.writeString(_gosnomer_type);
        parcel.writeString(_gosnomer_nomer);
        parcel.writeString(_gosnomer_number);
        parcel.writeString(_gosnomer_series);
        parcel.writeString(_gosnomer_region);
        parcel.writeString(_latitude);
        parcel.writeString(_longitude);
        parcel.writeString(_carmodel);
        parcel.writeString(_carmodel_id);
        parcel.writeString(_carmodel_manufacturer);
        parcel.writeString(_rating);
        parcel.writeString(_rating_plus);
        parcel.writeString(_rating_minus);
        parcel.writeString(_commentsnum);
        parcel.writeString(_countsgosnomer);
        parcel.writeString(_countscarmodel);
        parcel.writeString(_video_count);
        parcel.writeInt(this._picture_original.size());
        for (int k = 0; k < this._picture_original.size(); k++) {
            parcel.writeString(this._picture_original.get(k));
        }
        for (int k = 0; k < this._picture_medium.size(); k++) {
            parcel.writeString(this._picture_medium.get(k));
        }
        for (int k = 0; k < this._picture_small.size(); k++) {
            parcel.writeString(this._picture_small.get(k));
        }
        for (int k = 0; k < this._picture_tiny.size(); k++) {
            parcel.writeString(this._picture_tiny.get(k));
        }
        for (int k = 0; k < this._videos.size(); k++) {
            parcel.writeParcelable(this._videos.get(k), i);
        }
    }
    public static final Parcelable.Creator<Fact> CREATOR = new Parcelable.Creator<Fact>() {
    	 
        public Fact createFromParcel(Parcel in) {
          return new Fact(in);
        }
     
        public Fact[] newArray(int size) {
          return new Fact[size];
        }
      };
    public int describeContents() {
        return 0;
      }
/*    
    // get- и set-методы опущены для краткости
    public String getDate() {
        return FORMATTER.format(this.date);
    }
 
    public void setDate(String date) {
        // удлинение представления даты при необходимости
        while (!date.endsWith("00")){
            date += "0";
        }
        try {
            this.date = FORMATTER.parse(date.trim());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }*/
    @Override
    public String toString() {
             // реализация опущена для краткости
    	return "";
    }
 
    @Override
    public int hashCode() {
             // реализация опущена для краткости
    	return 0;
    }
    
    @Override
    public boolean equals(Object obj) {
             // реализация опущена для краткости
    	return false;
    }
      // сортировка по дате
    public int compareTo(Fact another) {
        if (another == null) return 1;
        // сортировка по убывания, наиболее свежие записи выводят сверху
        return another._datecreated.compareTo(this.getDatecreated());
    }

}