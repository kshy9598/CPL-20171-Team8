package com.example.kimdongho.myapplication.model;

/**
 * Created by KimDongHo on 2017-05-25.
 */

public class GpsPoint {
    private double longitude;
    private double latitude;

    public GpsPoint(double longitude, double latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
    }

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
}
