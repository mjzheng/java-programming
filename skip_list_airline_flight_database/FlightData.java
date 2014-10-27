
public class FlightData {
	
	public String flightNumber;
	public double price;
	
	public FlightData(String flightNumber, double price){
		this.flightNumber = flightNumber;
		this.price = price;
	}

	public String toString(){
		return flightNumber + " " + price;
	}
	
}
