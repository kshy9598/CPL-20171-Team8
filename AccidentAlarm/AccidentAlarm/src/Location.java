
public class Location {
	public double latitude;
	public double longitude;
	
	public Location(){	}
	
	@Override
	public boolean equals(Object obj){
		Location p = (Location)obj;
		
		if(latitude == p.latitude && longitude == p.longitude)
			return true;
		else
			return false;
	}
	
	public void copyLocation(Location loc){
		latitude = loc.latitude;
		longitude = loc.longitude;
	}
}
