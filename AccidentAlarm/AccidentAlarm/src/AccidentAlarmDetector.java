import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

// 2�� ��� ������ ���� �˶� ���� Ŭ����
public class AccidentAlarmDetector {
	private static final double ALARM_DISTANCE = 1; // 2�� ��� �˶��� �︮�� ���� ������� ��ġ�� ������ ��ġ�� �Ÿ�
	private static final int GPS_NUMBER = 5; // �������� �ֱ� GPS ���� ����
	private List<AccidentLocation> accidentList; // ��� ���� ��ġ ���� ����Ʈ
	private List<Location> gpsList;
	
	public AccidentAlarmDetector(){
		accidentList = new ArrayList<AccidentLocation>();
		gpsList = new ArrayList<Location>();
	}
	
	public List<Location> makeLocationList(List<Double> numberList)
	{
		List<Location> locationList = new ArrayList<Location>();
		
		for(int i = 0; i < numberList.size(); i += 2){
			locationList.add(new Location(numberList.get(i), numberList.get(i + 1)));
		}
		
		return locationList;
	}

	// ��� ���� ��ġ ���� ����
	public void setAccidentLocation(List<Location> locationList){
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
		for(int i = 0; i < locationList.size(); i++){
			accidentList.add(new AccidentLocation(locationList.get(i)));
		}
	}
	
	// GPS ����Ʈ ���� (�ֱ����κ��� GPS_NUMBER ���� GPS���� ���ܳ��´�)
	public void set_nowLocation(double my_latitude, double my_longitude)
	{
		if(gpsList.size() >= GPS_NUMBER){
			gpsList.remove(0);
		}
		gpsList.add(new Location(my_latitude, my_longitude));
	}
	
	// �� ��ǥ�� �Ÿ� ���
	public double calc_distance(double my_latitude, double my_longitude, double accident_latitude, double accident_longitude){
		double ret = Math.sqrt(Math.pow(Math.abs(my_latitude - accident_latitude), 2) + Math.pow(Math.abs(my_longitude - accident_longitude), 2));
		
		return ret;
	}
	
	// �˶� ���θ� ������ �Լ�
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