
public class FlightNode {

	private FlightKey key;
	private FlightData value;
	private FlightNode next, previous, down, up;
	
	public FlightNode(FlightKey key, FlightData value){
		this.key = key;
		this.value = value;
		next = null;
		previous = null;
		down = null;
		up = null;
	}
	
	public FlightKey getKey(){
		return key;
	}
	
	public FlightData getvalue(){
		return value;
	}
	
	public FlightNode next() {
    	return next;
    }
	
	public FlightNode previous() {
    	return previous;
    }
	
	public FlightNode down() {
    	return down;
    }
	
	public FlightNode up() {
    	return up;
    }
	
	public void setNext(FlightNode next){
		this.next = next; 
	}
	
	public void setPrevious(FlightNode previous){
		this.previous = previous; 
	}
	
	public void setDown(FlightNode down){
		this.down = down; 
	}
	
	public void setUp(FlightNode up){
		this.up = up; 
	}
	
	/*public String toString(){
		return key.toString() + " " + value.toString();
	}*/
	
}
