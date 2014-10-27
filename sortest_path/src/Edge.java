/** Edge class represents a single node in the linked list of edges for a vertex.
 * 
 */

class Edge {
  
    public int nextVertexId;
    public int weight;
/*    private String color;
*/  

   public Edge next;
    
    public Edge(int id, int weight){
    	this.nextVertexId = id;
    	this.weight = weight;
    	//this.color = color;
    	next = null;
    }

    public int getId(){
    	return nextVertexId;
    }
    public int getWeight(){
    	return weight;
    }
    /*public String getColor(){
    	return color;
    }*/
    public Edge getNext(){
    	return next;
    }
    public void setNext(Edge next){
    	this.next = next;
    }
    
 }