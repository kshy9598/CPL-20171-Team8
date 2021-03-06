package com.example.kimdongho.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KimDongHo on 2017-05-25.
 */

public class AccidentData implements Parcelable {
    private String username;
    private String phone;
    private String photo;
    private double longitude;
    private double latitude;
    private int checksound;

    public AccidentData(String username,String phone,String photo, double longitude, double latitude, int checksound)
    {
        this.username = username;
        this.phone = phone;
        this.photo = photo;
        this.longitude = longitude;
        this.latitude = latitude;
        this.checksound = checksound; //0 주변차량 사고발생 , 1 내차량 사고발생
    }

    protected AccidentData(Parcel in) {
        username = in.readString();
        phone = in.readString();
        photo = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        checksound = in.readInt();
    }

    public static final Creator<AccidentData> CREATOR = new Creator<AccidentData>() {
        @Override
        public AccidentData createFromParcel(Parcel in) {
            return new AccidentData(in);
        }
        @Override
        public AccidentData[] newArray(int size) {
            return new AccidentData[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(photo);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeInt(checksound);
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    
    public void setPhone(String phone){this.phone = phone;}
    
    public String getPhone() { return phone; }

    public void setPhoto(String photo){this.photo = photo;}

    public String getPhoto() { return photo; }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLogitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setChecksound(int checksound) {
        this.checksound = checksound;
    }

    public int getChecksound() {
        return checksound;
    }
}
