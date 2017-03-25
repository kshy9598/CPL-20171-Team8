package com.example.kimdongho.myapplication;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MyService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mDevices;
    private int mPairedDeviceCount;
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    Thread mWorkerThread;
    byte[] readBuffer;
    int readBufferPositon;

    MediaPlayer mp;

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
        mp = MediaPlayer.create(this, R.raw.chacha);
        mp.setLooping(false); // 반복재생
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d("test", "서비스의 onStartCommand");
        String device_name = intent.getStringExtra("device_name");
        connectToSelectedDevices(device_name);

        //mp.start(); // 노래 시작
        //AccidentCheck("name");
        //return super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        try {
            mWorkerThread.interrupt();   // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
            mp.stop(); // 음악 종료
        } catch (Exception e) {
        }
        Log.d("test", "서비스의 onDestroy");
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
            Toast.makeText(getApplicationContext(), "Connecft Error", Toast.LENGTH_LONG).show();
            onDestroy();
        }
    }

    void beginListenForData() {
        final Handler handler = new Handler();

        readBuffer = new byte[1024];
        ;  //  수신 버퍼
        readBufferPositon = 0;        //   버퍼 내 수신 문자 저장 위치

        // 문자열 수신 쓰레드

        mWorkerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {

                    try {
                        int bytesAvailable = mInputStream.available();    // 수신 데이터 확인
                        if (bytesAvailable > 0) {                     // 데이터가 수신된 경우
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];

                                if (b == '\n') {  //문자열 끝에 도달 시 들어감

                                    byte[] encodedBytes = new byte[readBufferPositon];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPositon = 0;

                                    handler.post(new Runnable() {

                                        public void run() {
                                            AccidentCheck(data);
                                            /////////////////////////////////////
                                            // 수신된 문자열 데이터에 대한 처리 작업
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPositon++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        // 데이터 수신 중 오류 발생.
                        onDestroy();//?
                    }
                }
            }
        });
        mWorkerThread.start();
        //출처: http://hyoin1223.tistory.com/entry/안드로이드-블루투스-프로그래밍 [lionhead]
    }

    void AccidentCheck(String data){
        Intent intent = new Intent(this,WarningActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("accdient_data",data);
        startActivity(intent);
    }
}