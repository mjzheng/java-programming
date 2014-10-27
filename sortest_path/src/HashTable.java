
public class HashTable {
	
	//hash table that uses open hashing
	// an array of linked-lists
	
	private class HashNode{
		// key
		String city;
		// value
		int id;
		HashNode next;
		
		public HashNode(String key, int data){
			city = key;
			id = data;
			next = null;
		}
		
		public HashNode next(){
			return next;
		}
		
		public void setNextHashNode(HashNode next){
			this.next = next;
		}
		
	}
	
	private HashNode[] table;
	private int size;
	
	// constructor
	public HashTable(int size){
		//create an array with specified size
		table = new HashNode[size];
		this.size = size;
	}
	
	public int size(){
		return size;
	}
	
	// takes in a String and turns it into int form and hashes it.
	private int hash(String key){
		int h = 0;

		for (int i = 0; i < key.length(); i++) {
			h += (int) key.charAt(i);
		}

		return h % size;

	}
	
	public void put(String key, int value){
		
		HashNode newNode = new HashNode(key, value);
		// have to use a hash function
		int index = hash(key);
		System.out.println(index);
		if (table[index] == null){
			table[index] = newNode;
		}else{
			HashNode temp = table[index]; 
			while(temp.next != null){
				temp = temp.next;
			}
			temp.next = newNode;
		}
	}
	
	public int get(String input){
		int index = hash(input);
		HashNode temp = table[index];
		if(temp.city.equals(input)){
			return temp.id;
		}
		while (temp.next!= null){
			if(temp.city.equals(input)){
				return temp.id;
			}
			temp = temp.next;
		}
		return -1;
	}
	
	public boolean containsKey(String input){
		int index = hash(input);
		HashNode temp = table[index];
		if(temp.city.equals(input)){
			return true;
		}
		while (temp.next!= null){
			if(temp.city.equals(input)){
				return true;
			}
			temp = temp.next;
		}
		return false;
	}
	
	/*public static void main(String[] args) {
		
		HashTable test = new HashTable(10);
		//System.out.println(test.size);
		test.put("San Francisco", 0);
		test.put("New York", 4);
		System.out.println(test.containsKey("San Francisco"));
		
	}*/
	
}
