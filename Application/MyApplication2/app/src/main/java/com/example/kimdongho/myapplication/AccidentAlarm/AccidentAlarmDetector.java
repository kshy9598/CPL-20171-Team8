package com.example.kimdongho.myapplication.AccidentAlarm;

/**
 * Created by KimDongHo on 2017-05-31.
 */
import com.example.kimdongho.myapplication.model.GpsPoint;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

// 2차 사고 방지를 위한 알람 관리 클래스
public class AccidentAlarmDetector {
    private static final double ALARM_DISTANCE = 1; // 2차 사고 알람을 울리기 위한 사고현장 위치와 운전자 위치의 거리
    private static final int GPS_NUMBER = 1; // 운전자의 최근 GPS 저장 개수
    private List<AccidentLocation> accidentList; // 사고 현장 위치 정보 리스트
    private List<Location> gpsList;

    public AccidentAlarmDetector(){
        accidentList = new ArrayList<AccidentLocation>();
        gpsList = new ArrayList<Location>();
    }

    //5월 31일 김동호 수정
    public List<Location> makeLocationList(ArrayList<GpsPoint> numberList)
    {
        List<Location> locationList = new ArrayList<Location>();

        for(int i = 0; i < numberList.size(); i += 1){
            //수정부분 List -> GpsPoint
            locationList.add(new Location(numberList.get(i).getLatitude(), numberList.get(i).getLogitude()));
        }

        return locationList;
    }

    // 사고 현장 위치 정보 갱신
    public void setAccidentLocation(List<Location> locationList){
        // 새로운 사고 정보 탐색 및 수습된 사고에 대한 정보 삭제
        for(int i = 0; i < accidentList.size(); i++){
            int index = -1;
            for(int k = 0; k < locationList.size(); k++){
                if(accidentList.get(i).equals(locationList.get(k))){
                    index = k;
                    locationList.remove(k);
                    break;
                }
            }

            if(index == -1){
                accidentList.remove(i);
                i--;
            }
        }

        // 새로운 사고정보 추가
        for(int i = 0; i < locationList.size(); i++){
            accidentList.add(new AccidentLocation(locationList.get(i)));
        }
    }

    // GPS 리스트 갱신 (최근으로부터 GPS_NUMBER 개의 GPS만을 남겨놓는다)
    public void set_nowLocation(double my_latitude, double my_longitude)
    {
        if(gpsList.size() >= GPS_NUMBER){
            gpsList.remove(0);
        }
        gpsList.add(new Location(my_latitude, my_longitude));
    }

    // 두 좌표의 거리 계산
    public double calc_distance(double my_latitude, double my_longitude, double accident_latitude, double accident_longitude){
        double ret = Math.sqrt(Math.pow(Math.abs(my_latitude - accident_latitude), 2) + Math.pow(Math.abs(my_longitude - accident_longitude), 2));

        return ret;
    }

    // 알람 여부를 측정한 함수
    public boolean check_alarm(){
        for(int i = 0; i < accidentList.size(); i++){
            if(accidentList.get(i).isAlarm() == true)
                continue;

            double first, second;
            first = calc_distance(gpsList.get(gpsList.size() - 1).getLatitude(), gpsList.get(gpsList.size() - 1).getLongitude(), accidentList.get(i).latitude, accidentList.get(i).longitude);
            if(first <= ALARM_DISTANCE){
                int check;
                for(check = gpsList.size() - 2; check >= 0; check--){
                    second = calc_distance(gpsList.get(check).getLatitude(), gpsList.get(check).getLongitude(), accidentList.get(i).latitude, accidentList.get(i).longitude);
                    if(first > second){
                        break;
                    }

                    first = second;
                }

                if(check == -1){
                    accidentList.get(i).setAlarm(true);
                    return true;
                }
            }
        }

        return false;
    }
}

