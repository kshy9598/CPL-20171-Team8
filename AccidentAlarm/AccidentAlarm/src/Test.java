import java.util.ArrayList;
import java.util.List;

public class Test {
	public static void main(String[] args){
		// 클래스 생성
		AccidentAlarmDetector alarmDetector = new AccidentAlarmDetector();
		
		// 사고 위도 경도를 Double형태의 List에 순서대로 넣는다 (위도, 경도, 위도, 경도 ...)
		List<Double> gpsList = new ArrayList<Double>();
		gpsList.add(10.0);
		gpsList.add(10.0);
		gpsList.add(11.0);
		gpsList.add(10.0);
		gpsList.add(20.0);
		gpsList.add(20.0);
		
		// 사고 정보를 갱신하기 위해 저장한 gpsList를 변환한 후 사고 정보를 갱신하는 함수를 호출한다.
		alarmDetector.setAccidentLocation(alarmDetector.makeLocationList(gpsList));
		
		// 현재 운전자의 GPS 정보를 보낸다
		alarmDetector.set_nowLocation(10.1, 10.1);

		// check_alarm함수로 사고 알람 여부를 확인할 수 있다.
		// true : 알람 필요, false : 알람 불필요
		System.out.println(alarmDetector.check_alarm());
	}
}
