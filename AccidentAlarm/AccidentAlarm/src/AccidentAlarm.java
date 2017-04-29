import java.util.ArrayList;
import java.util.List;

// 2차 사고 방지를 위한 알람 관리 클래스
public class AccidentAlarm {
	private static final double ALARM_DISTANCE = 1; // 2차 사고 알람을 울리기 위한 사고현장 위치와 운전자 위치의 거리
	private List<AccidentLocation> accidentList; // 사고 현장 위치 정보 리스트
	
	public AccidentAlarm(){
		accidentList = new ArrayList<AccidentLocation>();
	}
	
	// 사고 현장 위치 정보 갱신
	public void getAccidentLocation(List<Location> locationList){
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
		AccidentLocation temp;
		for(int i = 0; i < locationList.size(); i++){
			temp = new AccidentLocation(locationList.get(i));
			accidentList.add(temp);
		}
	}
	
	public double calc_distance(double my_latitude, double my_longitude, double accident_latitude, double accident_longitude){
		double ret = 0;
		
		return ret;
	}
	
	public void check_alarm(double my_latitude, double my_longitude){
		for(int i = 0; i < accidentList.size(); i++){
			if(calc_distance(my_latitude, my_longitude, accidentList.get(i).latitude, accidentList.get(i).longitude) <= ALARM_DISTANCE){
				/*
				추후 구현예정
				*/
				break;
			}
		}
	}
}
