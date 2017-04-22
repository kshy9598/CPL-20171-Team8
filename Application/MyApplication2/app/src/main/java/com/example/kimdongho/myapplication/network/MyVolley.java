package com.example.kimdongho.myapplication.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by KimDongHo on 2017-04-22.
 */

public class MyVolley {
    private static MyVolley volley;
    private RequestQueue requestQueue;


    private MyVolley(Context context) {
        requestQueue = Volley.newRequestQueue(context);

    }

    public static MyVolley getInstance(Context context) {
        if(volley == null){
            volley = new MyVolley(context);
        }

        return volley;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}

