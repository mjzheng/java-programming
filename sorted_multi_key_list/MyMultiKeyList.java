import java.util.NoSuchElementException;




public class MyMultiKeyList implements MultiKeyList{
	
	// pointer to start of list
	public Node head[];
	// pointer to end of list
	public Node tail[];
	public int length;
	public int numKeys;

	/**
	 * The constructor for MyMultiKeyList should take a single integer parameter -- the number of keys in the list.
	 *  If, for instance, we create a list with 3 keys, then every node in the list needs to contain exactly 3 keys.
	**/
	public MyMultiKeyList(int numKeys){
		// right now have empty array of Nodes heads and tails
		head = new Node[numKeys];
		tail = new Node[numKeys]; 
		length = 0;
		this.numKeys = numKeys;
	}

    // Adds an object to the list.  If the length of keys is not the same
    // as the number of keys in the list, throw an IllegalArgumentException
	@Override
	public void add(Comparable[] keys, Object data) {
		
		// base case first check of the length of keys are the same
		if (keys.length != numKeys){
			throw new IllegalArgumentException();
		}
		
		// make new Node
		Node newNode = new Node(numKeys, keys, data);
		// for each key element / meaning for each head Node
		for (int i=0; i<numKeys; i++){

			// base case of empty list
			if (length == 0){
				//System.out.println(1);
				head[i] = tail[i] = newNode;
			}
			
			// if equal, just put newNode behind matching node
			
			// base case of making newNode the head
			// Compare new Node with the head
			// if newNode is smaller, put it before head
			else if(newNode.key(i).compareTo(head[i].key(i)) <= 0){
				//System.out.println(2);

				newNode.next[i] = head[i];
				head[i].prev[i] = newNode;
				head[i] = head[i].prev[i];
			}

			// base case of making newNode the tail
			// Compare newNode with the tail
			// if newNode is greater than tail, place it after tail
			else if(newNode.key(i).compareTo(tail[i].key(i)) >= 0){
				//System.out.println(3);

				newNode.prev[i] = tail[i];
				tail[i].next[i] = newNode;
				tail[i] = tail[i].next[i];
				
				//System.out.println(newNode.key(i)+" "+ tail[i].key(i));
			}
			
			// regular case
			else{
				//System.out.println(4);

				//System.out.println(data);
				
				Node current = head[i];
				//System.out.println(current.data() +  " " + current.key(i));
				
				Boolean test = false;
				while (current.next != null && test == false){
					// compare current
					current = current.next[i];

					// if current is greater than newNode, 
					//System.out.println(current.key(i).compareTo(newNode.key(i)));
					if (current.key(i).compareTo(newNode.key(i)) >= 0){
						
						// current .prev is false
						//System.out.println(current.prev[i] != null);
						//System.out.println(i);
						
						Node temp = current;
						Node temp2 = current.prev[i];
						current = newNode;
						temp2.next[i] = current;
						current.prev[i]= temp2;
						current.next[i] = temp;
						temp.prev[i]= current;
						

						test = true;
					}
					//System.out.println(current.key(i) + " - "+ newNode.key(i) +" - "+ current.data);
				}
			}
		}
		
		// update length
		length += 1;
		//System.out.println(length);
	}
	
	
	/**
	 *Get an iterator to iterate over a particular key.  If keyIndex is not
	 *within the range of allowed keys, throw an IllegalArgumentException 
	 */
	@Override
	public MultiKeyListIterator iterator(int keyIndex) {
		try{
			return new InnerIterator(keyIndex);
		}catch(IllegalArgumentException e){
			throw new IllegalArgumentException();
		}
	}

    // Get an interface to the element at a particular index of the list.  If keyIndex is not
    // within the range of allowed keys, throw an IllegalArgumentException
	@Override
	public ListElem get(int index, int keyIndex) {
		if (index >= length || index <0 || keyIndex >= numKeys || keyIndex < 0){
			throw new IllegalArgumentException();
		}
		int count = 0;
		Node temp = head[keyIndex];
		if (count == index){
			return temp;
		}else{
			while (temp.next[keyIndex] != null){
				temp = temp.next[keyIndex];
				count += 1;
				if (count == index){
					return temp;
				}
			}
			//System.out.println("null");
			return null;
		}
		
	}
	
	public void helperRemove(Node elem){
		try{
			for (int i=0; i<numKeys; i++){
				if(elem.prev[i] != null){
					elem.prev[i].next[i] = elem.next[i];
				}else{
					head[i] = elem.next[i];
				}
				if(elem.next[i] != null){
					elem.next[i].prev[i] = elem.prev[i];
				}else{
					tail[i] = elem.prev[i];
				}
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	/**
	 *Remove the ith element in the list using the given key index. 
	 */
	@Override
	public void removeIndex(int index, int keyIndex) {
		
		try{
			int count = 0;
			Node temp = head[keyIndex];
			while(count < index){
				count += 1;
				temp = temp.next[keyIndex];
			}
			helperRemove(temp);
		}catch(Exception e){
			throw new IllegalArgumentException();
		}		
	}
	
	public boolean findMatch(Comparable[] keys){
		
	}

	/**
	 * Remove the element matching *all* keys 
	 */
	@Override
	public void remove(Comparable[] keys) {
		
		
		
	}

	/**
	 * Remove the element matching the key at the given index  
	 */
	@Override
	public void remove(Comparable key, int keyIndex) {
		
		
	}
	
	public class Node implements ListElem{
		
		private int keys;
		private Comparable[] keyElements;
		private Object data;
		
		private Node[] prev;
		private Node[] next;
		
		public Node(int numKeys, Comparable[] keyElem, Object data){
			keys = numKeys;
			keyElements = new Comparable[keys];
			for (int i=0; i< keys ;i++){
				keyElements[i] = keyElem[i];
			}
			this.data = data;
			prev = new Node[keys];
			next = new Node[keys];
		}


		/**
		 * Returns the number of keys in this list element
		 */
		@Override
		public int numKeys() {
			return keys;
		}

		/**
		 * Returns the key at a given index
		 */
		@Override
		public Comparable key(int index) {
			return keyElements[index];
		}

		/**
		 * Returns the data associated with the element
		 */
		@Override
		public Object data() {
			return data;
		}
		
		
	}
	
	public class InnerIterator implements MultiKeyListIterator{
				
		private Node delete;
		private Node previous;
		// the next is actually current
		private Node next;
		
		private int keyIndex;
		
		// Constructor 
		public InnerIterator(int keyIndex){
			// assign values to all variables
			this.keyIndex = keyIndex;
			next = head[keyIndex];
			previous = null;
			delete = null;
		}

	    // Returns true if there is a next element.  If hasNext returns false, then next should
	    // throw a NoSuchElement exception
		@Override
		public boolean hasNext() {
			if (next != null){
				return true;
			}else{
				return false;
			}
		}

	    // Returns the next element in the list, and move the cursor position forward
	    // Throws a NoSuchElementException if there is no next element
		@Override
		public ListElem next() {
			if (next != null ){
				Node temp = next;
				previous = next;
				delete = next;
				if (next.next[keyIndex] == null){
					next = null;
				}else{
					next = next.next[keyIndex];
				}
				return temp;
			}else{
				throw new NoSuchElementException();
			}
		}

	    // Returns true if there is a previous element.  If hasPrevious returns false, then previous should
	    // throw a NoSuchElement exception
		@Override
		public boolean hasPrevious() {
			if (previous != null){
				return true;
			}else{
				return false;
			}
		}

	    // Returns the previous element in the list, and move the cursor position backwards
	    // Throws a NoSuchElementException if there is no previous element
	    // Alternating calls to next and previous will return the same element
		@Override
		public ListElem previous() {
			if (previous != null){
				Node temp = previous;
				next = previous;
				delete = temp;
				if (previous.prev[keyIndex] == null){
					previous = null;
				}else{
					previous = previous.prev[keyIndex];
				}
				return temp;
			}else {
				throw new NoSuchElementException();
			}
			
		}

		// Removes the element last returned by next() or previous().  If remove is
	    // called before next is called, or if remove is called twice in a row 
	    // without an intervening call to next or previous, then an
	    // IllegalStateExeception is thrown  
		@Override
		public void remove() {
			if (delete != null){
				// do all the update on next, previous,
				helperRemove(delete); 
				length = length -1;
				delete = null;
			}else{
				throw new IllegalStateException();
			}
		}
		
	}

}
