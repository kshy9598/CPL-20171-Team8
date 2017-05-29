package com.example.kimdongho.myapplication;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.kimdongho.myapplication.model.AccidentData;
import com.example.kimdongho.myapplication.model.GpsPoint;
import com.example.kimdongho.myapplication.network.NetworkUtil;
import com.example.kimdongho.myapplication.util.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.String;
import java.io.FileOutputStream;

public class MyService extends Service {
    //BlueTooth
    private static final String TAG = "MyService_GPS_TEST";
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mDevices;
    private int mPairedDeviceCount;
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    private int bluetoothState;

    //Thread
    Thread mWorkerThread;
    byte[] readBuffer;
    int readBufferPositon;

    //Network, AccidentGpsPointList
    private NetworkUtil networkUtil;
    private String username = "KimDongHo";
    private ArrayList<GpsPoint> GpsPointList;

    //My GpsPoint
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int LOCATION_INTERVAL = 0; //1000 = 1second
    private static final float LOCATION_DISTANCE = 0; // M단위
    private double longitude;
    private double latitude;

    //AccidentData
    private AccidentData accidentData = new AccidentData("no",0,0);

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("test", "서비스의 onCreate");
        bluetoothState = 0;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행

        //네트워크 설정
        networkUtil = new NetworkUtil(getApplicationContext());
        //GPS 설정
        settingGps();
        getGPS();

        Log.e(TAG,""+longitude);
        String device_name = intent.getStringExtra("device_name");
        connectToSelectedDevices(device_name);

        //return super.onStartCommand(intent, flags, startId);
        //return START_REDELIVER_INTENT;
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        try {
            mWorkerThread.interrupt();   // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    // 클릭한 디바이스 장치 이름에 대해 객체 반환 (장치이름은 메인에서 intent로 받음)
    BluetoothDevice getDeviceFromBondedList(String name) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();
        BluetoothDevice selectedDevice = null;

        for (BluetoothDevice device : mDevices) {
            if (name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    //장치 연결
    void connectToSelectedDevices(String selectedDeviceName) {
        BluetoothDevice mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            // 소켓 생성
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            // RFCOMM 채널을 통한 연결
            mSocket.connect();
            // 데이터 송수신을 위한 스트림 열기
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();
            // 데이터 수신 준비
            beginListenForData();
        } catch (Exception e) {
            // 블루투스 연결 중 오류 발생
            Toast.makeText(getApplicationContext(), "Connect Error", Toast.LENGTH_LONG).show();
            //onDestroy();
        }
    }

    void receiveImageFile(InputStream is){
        String filename = "/home/CARS/accidentImage.jpg";

        try {
            Log.v("rFile1", "start");
            FileOutputStream fos = new FileOutputStream(filename);
            Log.v("rFile3", "mid2");
            byte[] buffer = new byte[8192];
            Log.v("rFile4", "mid3");
            int readBytes;

            Log.v("rFile5", "mid4");
            while((readBytes = is.read(buffer)) > 0){
                fos.write(buffer, 0, readBytes);
            }

            Log.v("rFile10", "end");
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        readBuffer = new byte[1024];
        ;  //  수신 버퍼

        readBufferPositon = 0;        //   버퍼 내 수신 문자 저장 위치
        Toast.makeText(getApplicationContext(), "Connect BlueTooth", Toast.LENGTH_LONG).show();
        // 문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable() {
            public void run() {
                FileOutputStream fos = null;
                String filename = "/data/data/com.example.kimdongho.myapplication/accidentImage.jpg";
                byte[] packetBytes = new byte[8192];

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        int bytesAvailable = mInputStream.read(packetBytes);    // 수신 데이터 확인
                        Log.v("rFile", Integer.toString(bluetoothState) + " " + Integer.toString(bytesAvailable));
                        StringBuffer sb = new StringBuffer();
                        sb.append(new String(packetBytes, 0, bytesAvailable));
                        String tempStr = sb.toString();
                        if(tempStr.equals("accident") == true){
                            bluetoothState = 2;
                        }

                        if (bytesAvailable > 0 && bluetoothState == 0){
                            fos = new FileOutputStream(filename);
                            bluetoothState = 1;
                            Log.v("rFile1", "start");
                        }
                        if (bytesAvailable > 0 && bluetoothState == 1) {                     // 데이터가 수신된 경우
                            //int bytesRead = mInputStream.read(packetBytes);
                            fos.write(packetBytes, 0, bytesAvailable);
                            Log.v("rFile3", "mid");
                        }
                        if(bluetoothState == 2){
                            Log.v("rFile10", "end");
                            fos.close();
                            handler.post(new Runnable() {

                                public void run() {
                                    //receiveImageFile(mInputStream);

                                    Toast.makeText(getApplicationContext(), "AccidentCheck Start", Toast.LENGTH_LONG).show();

                                    //서버로 전송할 데이터를 AccidentData 객체에 넣는다.
                                    accidentData.setUsername(username);
                                    accidentData.setLongitude(longitude);
                                    accidentData.setLatitude(latitude);

                                    AccidentCheck(accidentData);
                                    /////////////////////////////////////
                                    // 수신된 문자열 데이터에 대한 처리 작업
                                }
                            });
                            bluetoothState = 0;
                        }
                    } catch (IOException ex) {
                        // 데이터 수신 중 오류 발생.
                        onDestroy();
                    }
                }
            }
        });
        mWorkerThread.start();
    }

    void AccidentCheck(AccidentData data){
        Intent intent = new Intent(this,WarningActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("AccdientData",data);
        startActivity(intent);
    }

    public void settingGps(){
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            //위치 변경 감지 될 시 서버에 GpsPointList를 Get 형식으로 요청한다.
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.e(TAG, "longitude = " + longitude + "latitude = " + latitude);
                Toast.makeText(getApplicationContext(),"Respone_Server", Toast.LENGTH_LONG).show();
                requestGetGps();
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }
    public void getGPS(){
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    locationListener
            );
        }  catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    locationListener
            );
        }  catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }

    //Volley Jason Part
    public void requestGetGps()
    {
        Config config = new Config();
        //String url = config.encodingUrl(Config.MAIN_URL+Config.GET_RESTAURANT, "food_name", food_name);
        String url = config.encodingUrl(Config.MAIN_URL, "username", username);
        networkUtil.requestServer(url,
                null,
                networkSuccessListener(),
                networkErrorListener());
    }

    public void getJsonArray(JSONObject response)
    {
        try {
            JSONArray jsonArray = response.getJSONArray("GpsPointList");

            //GpsPointList_DataInput
            int size = jsonArray.length();
            for(int i = 0; i<size; i++){
                JSONObject data = jsonArray.getJSONObject(i);
                GpsPoint point = new GpsPoint(data.getDouble("longitude"),data.getDouble("latitude"));
                GpsPointList.add(i, point);
            }
        } catch (JSONException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to parse the String: No" );
        }
    }

    private Response.Listener<JSONObject> networkSuccessListener() {
        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                //GPSPointList 획득
                getJsonArray(response);
                //2차 사고 예방 감지 프로그램에 GpsPointList 넣기
                //성현이와 미팅 필요

                //List Clear
                GpsPointList.clear();
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