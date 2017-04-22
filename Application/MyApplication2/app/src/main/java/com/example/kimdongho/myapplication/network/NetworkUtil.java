package com.example.kimdongho.myapplication.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by KimDongHo on 2017-04-22.
 */
public class NetworkUtil {
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;

    public NetworkUtil(Context context)
    {
        requestQueue = MyVolley.getInstance(context).getRequestQueue();
    }

    /*      POST, PUT, DELETE       */
    public void requestServer(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        jsonObjectRequest = new JsonObjectRequest(method, url, jsonRequest, listener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }

    /*       GET        */
    public void requestServer(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        jsonObjectRequest = new JsonObjectRequest( url, jsonRequest, listener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }
}

