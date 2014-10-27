
public class FlightKey implements Comparable<FlightKey>{

	private String origin;
	private String destination;
	private String date;
	private String time;
		
	public FlightKey(String origin, String destination, String date, String time){
		this.origin = origin;
		this.destination = destination;
		this.date = date;
		this.time = time;
	}
	
	public String getOrigin(){
		return origin;
	}
	
	public String getDest(){
		return destination;
	}
	
	public String getDate(){
		return date;
	}
	
	public String getTime(){
		return time;
	}
	
	@Override
	public int compareTo(FlightKey other) {
		//checks origin
		if (this.origin.compareToIgnoreCase(other.origin) < 0){
			return -1;
		}else if(this.origin.equals(other.origin)){
			//checks destination
			if(this.destination.compareToIgnoreCase(other.destination) < 0){
				return -1;
			}else if(this.destination.equals(other.destination)){
				//checks date
				// rearrange from 03/15/2014 to 2014/03/15
				String thisTemp = rearrangeDate(this.date);
				String otherTemp = rearrangeDate(other.date);
				if(thisTemp.compareToIgnoreCase(otherTemp) < 0){
					return -1;
				}else if(thisTemp.equals(otherTemp)){
					//checks location
					return this.time.compareToIgnoreCase(other.time);
				}else{
					return 1;
				}
				
			}else{
				return 1;
			}
		}
		else{
			return 1;
		}
	}
	
	// Rearranges dates from month/day/year to year/month/day
	public String rearrangeDate(String date){
		String year = date.substring(6);
		String temp = date.substring(0, 5);
		return year + "/" + temp;
	}
	
	public String toString(){
		return origin + " " + destination + " " + date + " " + time ;
	}

}
