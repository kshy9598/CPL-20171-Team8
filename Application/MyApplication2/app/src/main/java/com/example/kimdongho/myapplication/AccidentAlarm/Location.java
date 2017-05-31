package com.example.kimdongho.myapplication.AccidentAlarm;

/**
 * Created by KimDongHo on 2017-05-31.
 */


public class Location {
    public double latitude;
    public double longitude;

    public Location(){	}
    public Location(double my_latitude, double my_longitude){
        latitude = my_latitude;
        longitude = my_longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object obj){
        Location p = (Location)obj;

        if(latitude - p.latitude < 0.000000001 && longitude - p.longitude < 0.000000001)
            return true;
        else
            return false;
    }

    public void copyLocation(Location loc){
        latitude = loc.latitude;
        longitude = loc.longitude;
    }
}

