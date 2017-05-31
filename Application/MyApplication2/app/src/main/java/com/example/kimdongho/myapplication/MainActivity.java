package com.example.kimdongho.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1 ;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mDevices;
    private int mPairedDeviceCount;

    final int MY_PERMISSION_REQUEST_AUDIO = 3;
    final int MY_PERMISSION_REQUEST_GPS = 4;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkPermission(); //음성인식 권한 요청
        checkBlueTooth(); //블루투스 체크

        /*
        //Test 5월 31일
        Intent intent = new Intent(
                getApplicationContext(),//현재제어권자
                MyService.class); // 이동할 컴포넌트
        startService(intent); // 서비스 시작
        */

        ImageView b1 = (ImageView) findViewById(R.id.startButton);

        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkBlueTooth(); //블루투스 체크
            }
        });

    } // end of onCreate
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Need GPS permission", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_GPS);
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Need audio permission", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSION_REQUEST_AUDIO);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_GPS:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Get permission", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Permission", "Permission always deny");
                        }
                    }
                }
                break;
            case MY_PERMISSION_REQUEST_AUDIO:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.RECORD_AUDIO)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Get Audio permission", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Permission", "Permission always deny");
                        }
                    }
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void checkBlueTooth() {
        if(mBluetoothAdapter == null) {
            // 장치가 블루투스 지원하지 않는 경우
            Toast.makeText(getApplicationContext(),"The device does not support Bluetooth ",Toast.LENGTH_LONG).show();
            finish();   // 어플리케이션 종료
        }

        else {
            // 장치가 블루투스 지원하는 경우
            if(!mBluetoothAdapter.isEnabled()) {
                // 블루투스를 지원하지만 비활성 상태인 경우
                // 블루투스를 활성 상태로 바꾸기 위해 사용자 동의 요첨
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                // 블루투스를 지원하며 활성 상태인 경우
                // 페어링된 기기 목록을 보여주고 연결할 장치를 선택.
                Toast.makeText(getApplicationContext(),"Already on bluetooth", Toast.LENGTH_SHORT).show();
                selectDevice();
            }

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) {
                    // 블루투스가 활성 상태로 변경됨
                    Toast.makeText(getApplicationContext(),"Turned on bluetooth",Toast.LENGTH_SHORT).show();
                    selectDevice();
                }

                else if(resultCode == RESULT_CANCELED) {
                    // 블루투스가 비활성 상태임
                    Toast.makeText(getApplicationContext(),
                            "If you do not turn on Bluetooth, you will not be able to run the app.",Toast.LENGTH_LONG).show();
                    finish();  //  어플리케이션 종료

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //연결할 디바이스 선택
    @RequiresApi(api = Build.VERSION_CODES.M)
    void selectDevice() {
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();

        if(mPairedDeviceCount == 0 ) {
            Toast.makeText(getApplicationContext(),"Not Pairing",Toast.LENGTH_SHORT).show();
            //  페어링 된 장치가 없는 경우
            //5월 24일자 확인
            //finish();    // 어플리케이션 종료
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 페어링 된 블루투스 장치의 이름 목록 작성
        List<String> listItems = new ArrayList<String>();

        for(BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }
        listItems.add("취소");    // 취소 항목 추가

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == mPairedDeviceCount) {
                    // 연결할 장치를 선택하지 않고 '취소'를 누른 경우
                    //finish();
                } else {
                    // 연결할 장치를 선택한 경우
                    // 선택한 장치와 연결을 시도함
                    Intent intent = new Intent(
                            getApplicationContext(),//현재제어권자
                            MyService.class); // 이동할 컴포넌트
                    intent.putExtra("device_name",items[item].toString());//선택한 장치 네임 전달
                    startService(intent); // 서비스 시작
                }
            }
        });
        builder.setCancelable(false);    // 뒤로 가기 버튼 사용 금지
        AlertDialog alert = builder.create();
        alert.show();
    }
} // end of class
