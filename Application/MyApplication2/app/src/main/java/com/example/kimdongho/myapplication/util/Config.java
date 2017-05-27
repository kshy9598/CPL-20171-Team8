package com.example.kimdongho.myapplication.util;


import android.net.Uri;

/**
 * Created by KimDongHo on 2017-04-22.
 */

public class Config {

    //public final static String MAIN_URL= "http://ec2-52-42-143-232.us-west-2.compute.amazonaws.com";
    //public final static String MAIN_URL = "http://210.118.75.133";
    public final static String MAIN_URL = "http://52.79.214.37:8080";

    public final static String POST_SIGNIN = "/api/user/signin";
    public final static String POST_SIGNUP = "/api/user";

    public final static String GET_FOOD  = "/api/food";
    public final static String POST_EAT = "/api/eat";
    public final static String GET_EAT = "/api/eat";
    public final static String POST_REQUEST = "/api/request";
    public final static String GET_RESTAURANT = "/api/restaurant";

    public String encodingUrl(String url, String key, String value)
    {
        return Uri.parse(url).buildUpon().appendQueryParameter(key,value)
                .build().toString();
    }

    public String encodingUrl(String url, String segment)
    {
        return Uri.parse(url).buildUpon().appendPath(segment).build().toString();
    }
}
