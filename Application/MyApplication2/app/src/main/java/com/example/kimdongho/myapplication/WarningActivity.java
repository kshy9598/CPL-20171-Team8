package com.example.kimdongho.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.kimdongho.myapplication.model.AccidentData;
import com.example.kimdongho.myapplication.network.NetworkUtil;
import com.example.kimdongho.myapplication.util.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class WarningActivity extends Activity {

    public static final int REQUEST_CODE  = 1001;
    private SoundPool sound;
    public int soundId;
    private int streamId;
    private MainControl mainControl;
    SoundLoadComplete soundLoadComplete;
    WarningSound warningSound;
    public boolean isAlarmOK;
    public boolean afterFinish;
    private AnimationDrawable animDrawable;
    private ImageView warningImageView2;
    private boolean isGetResult;

    //PostNetWork
    private NetworkUtil networkUtil;
    private boolean serverAuth;

    //AccidentData
    private AccidentData accidentData;

    @Override
    public void onBackPressed() {
        sound.autoPause();
        sound.release();
        mainControl.setIsRunning(false);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            isGetResult = true;
            if(resultCode == RESULT_OK){
                boolean pass = data.getExtras().getBoolean("checkSound");
                if(pass == false){
                    //isAlarmOK = true;
                    networkUtil = new NetworkUtil(getApplicationContext());
                    sound.stop(streamId);
                    requestPostAccInfo();
                    SystemClock.sleep(1000);
                    finish();
                }else if(pass == true){
                    sound.stop(streamId);
                    SystemClock.sleep(1000);
                    Toast.makeText(getApplicationContext(), "신고가 취소 되었습니다.", Toast.LENGTH_LONG).show();
                    Log.d("activity_change", "warning to face tracker in onActivityResult");
                    finish();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        warningImageView2 = (ImageView)findViewById(R.id.warningImageView2);
        isAlarmOK = true;
        afterFinish = false;
        isGetResult = false;
        streamId = -1;

        sound = new SoundPool(1, 3, 0);// maxStreams, streamType, srcQuality
        soundId = sound.load(this, R.raw.alarm2, 1);
        mainControl = new MainControl();
        mainControl.start();

        accidentData = getIntent().getParcelableExtra("AccidentData");
        Log.e("-------------------", accidentData.getUsername());
    }

    private class MainControl extends Thread {
        private AtomicBoolean isRunning;
        @Override
        public void run() {
            isRunning = new AtomicBoolean(true);
            while (isRunning.get()) {
                soundLoadComplete = new SoundLoadComplete();
                soundLoadComplete.start();
                try {
                    soundLoadComplete.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (isRunning.get()) {
                    if (isAlarmOK) {
                        isAlarmOK = false;
                        warningSound = new WarningSound();
                        warningSound.start();
                        try {
                            warningSound.join();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(isRunning.get() == true) {
                            Log.d("activity_change", "warning to sound check in main control");
                            Intent soundCheckIntent = new Intent(getApplicationContext(), SoundCheckActivity.class);
                            soundCheckIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivityForResult(soundCheckIntent, REQUEST_CODE);
                            isGetResult = false;
                            while (isGetResult == false) {};

                        }
                    }
                }
            }
        }
        public void setIsRunning(boolean state){
            isRunning.set(state);
        }
    }

    @Override
    protected void onDestroy() {
        sound.autoPause();
        sound.release();
        super.onDestroy();
    }

    private class WarningSound extends Thread{

        long startTime;
        long endTime;
        long elapsedTime;

        public void run() {

            startTime = 0;
            endTime = 0;
            elapsedTime = 0;
            streamId = sound.play(soundId, 0.5F, 0.5F, 1, -1, 1.0F);
            startTime = System.currentTimeMillis();


            while (startTime == 0) {
            }
            while (elapsedTime < 5) {
                endTime = System.currentTimeMillis();
                elapsedTime = (int) (endTime - startTime) / 1000;
            }
            sound.stop(streamId);
        }
    }
    private class SoundLoadComplete extends Thread {
        boolean loadOK;

        @Override
        public void run() {
            loadOK = false;
            sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    loadOK = true;
                }
            });
            while (loadOK == false) {}
        }
    }

    //Image to String
    private String getStringFromBitmap(Bitmap bitmapPicture) {
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    //Volley Jason Part
    public void requestPostAccInfo()
    {
        try {
            JSONObject jsonObject = new JSONObject();
            //Bitmap accidentImage = BitmapFactory.decodeFile(accidentData.getPhoto());

            //전송할 사고 정보를 JsonObject에 담는다.
            jsonObject.put("username",accidentData.getUsername());
            jsonObject.put("phone",accidentData.getPhone());

            Toast.makeText(getApplicationContext(),"이미지 정보를 담고 있습니다", Toast.LENGTH_SHORT).show();
            jsonObject.put("photo",getStringFromBitmap(BitmapFactory.decodeFile(accidentData.getPhoto())));
            jsonObject.put("longitude",accidentData.getLogitude());
            jsonObject.put("latitude",accidentData.getLatitude());

            networkUtil.requestServer(Request.Method.POST,
                    // Config.MAIN_URL+Config.POST_SIGNIN,
                    Config.MAIN_URL,
                    jsonObject,
                    networkSuccessListener(),
                    networkErrorListener());

        } catch (JSONException e)
        {
            throw new IllegalStateException("Failed to convert the object to JSON");
        }
    }

    private Response.Listener<JSONObject> networkSuccessListener() {
        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    serverAuth = response.getBoolean("success");
                    if(serverAuth) {
                        Toast.makeText(getApplicationContext(),"신고가 접수 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    //throw new IllegalArgumentException("Failed to parse the String: " + serverMsg);
                }
                finally {
                }
            }
        };
    }
    private Response.ErrorListener networkErrorListener() {
        return new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }
}



