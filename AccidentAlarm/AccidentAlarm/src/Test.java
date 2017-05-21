import java.util.ArrayList;
import java.util.List;

public class Test {
	public static void main(String[] args){
		// Ŭ���� ����
		AccidentAlarmDetector alarmDetector = new AccidentAlarmDetector();
		
		// ��� ���� �浵�� Double������ List�� ������� �ִ´� (����, �浵, ����, �浵 ...)
		List<Double> gpsList = new ArrayList<Double>();
		gpsList.add(10.0);
		gpsList.add(10.0);
		gpsList.add(11.0);
		gpsList.add(10.0);
		gpsList.add(20.0);
		gpsList.add(20.0);
		
		// ��� ������ �����ϱ� ���� ������ gpsList�� ��ȯ�� �� ��� ������ �����ϴ� �Լ��� ȣ���Ѵ�.
		alarmDetector.setAccidentLocation(alarmDetector.makeLocationList(gpsList));
		
		// ���� �������� GPS ������ ������
		alarmDetector.set_nowLocation(10.1, 10.1);

		// check_alarm�Լ��� ��� �˶� ���θ� Ȯ���� �� �ִ�.
		// true : �˶� �ʿ�, false : �˶� ���ʿ�
		System.out.println(alarmDetector.check_alarm());
	}
}
