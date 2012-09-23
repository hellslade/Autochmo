package ru.android.hellslade.autochmo;

import android.os.Parcel;
import android.os.Parcelable;

public class Carmodel implements Parcelable {

    private String _model_id;
    private String _model_name;
    private String _manufacturer_id;
    private String _manufacturer_name;
    
    public Carmodel() {
    }
    public Carmodel(Parcel in) {
        this._model_id = in.readString();
        this._model_name = in.readString();
        this._manufacturer_id = in.readString();
        this._manufacturer_name = in.readString();
    }
    public void setModelId(String modelId) {
        this._model_id = modelId;
    }
    public String getModelId() {
        return this._model_id;
    }
    public void setModelName(String modelName) {
        this._model_name = modelName;
    }
    public String getModelName() {
        return this._model_name;
    }
    public void setManufacturerId(String manufacturerId) {
        this._manufacturer_id = manufacturerId;
    }
    public String getManufacturerId() {
        return this._manufacturer_id;
    }
    public void setManufacturerName(String manufacturerName) {
        this._manufacturer_name = manufacturerName;
    }
    public String getManufacturerName() {
        return this._manufacturer_name;
    }
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    public String toString() {
        return String.format("%s %s", this._manufacturer_name, this._model_name);
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_model_id);
        parcel.writeString(_model_name);
        parcel.writeString(_manufacturer_id);
        parcel.writeString(_manufacturer_name);
    }
    public static final Parcelable.Creator<Carmodel> CREATOR = new Parcelable.Creator<Carmodel>() {
        
        public Carmodel createFromParcel(Parcel in) {
          return new Carmodel(in);
        }
     
        public Carmodel[] newArray(int size) {
          return new Carmodel[size];
        }
      };
}
