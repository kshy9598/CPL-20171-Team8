
public class AccidentLocation extends Location {
	public boolean alarm;
	
	public AccidentLocation(){
		super();
		alarm = false;
	}
	
	public AccidentLocation(Location loc){
		super();
		super.copyLocation(loc);
		alarm = false;
	}

	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}
}
