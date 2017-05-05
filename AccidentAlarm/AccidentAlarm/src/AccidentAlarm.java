import java.util.ArrayList;
import java.util.List;

// 2�� ��� ������ ���� �˶� ���� Ŭ����
public class AccidentAlarm {
	private static final double ALARM_DISTANCE = 1; // 2�� ��� �˶��� �︮�� ���� ������� ��ġ�� ������ ��ġ�� �Ÿ�
	private List<AccidentLocation> accidentList; // ��� ���� ��ġ ���� ����Ʈ
	
	public AccidentAlarm(){
		accidentList = new ArrayList<AccidentLocation>();
	}
	
	// ��� ���� ��ġ ���� ����
	public void getAccidentLocation(List<Location> locationList){
		// ���ο� ��� ���� Ž�� �� ������ ����� ���� ���� ����
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
		
		// ���ο� ������� �߰�
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
				���� ��������
				*/
				break;
			}
		}
	}
}