import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;

public class FlightList {

	// head and tail are dummy elements
	private FlightNode start;
	// need current level
	private int numLevel;

	// find current level of given node
	public int getCurrentLevel(FlightNode node) {
		int count = 1;
		FlightNode temp = node;
		while (temp.down() != null) {
			count += 1;
			temp = temp.down();
		}
		return count;
	}

	// decides the number of level the new node has
	public int randomLevel() {
		int level = 1;
		Random rm = new Random();
		while ((rm.nextInt(2) + 0) == 1) {
			level += 1;
		}
		return level;
	}

	// Constructors
	public FlightList() {

		// empty list on top
		start = new FlightNode(null, null);
		start.setNext(new FlightNode(null, null));
		numLevel = 1;
	}

	public FlightList(String filename) {

		// empty list on top
		start = new FlightNode(null, null);
		start.setNext(new FlightNode(null, null));
		numLevel = 1;

		// open file and read file and need argument parser
		Charset charset = Charset.forName("UTF-8");
		File file = new File(filename);
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(),
				charset)) {
			String line = null;
			// while it's not end of file
			while ((line = reader.readLine()) != null) {

				// ex of line = FRA JFK 05/05/2014 07:00 LH122 300
				// need to split this line by space into an array
				String[] temp = line.split(" ");
				// elements 1-4 = FlightKey
				FlightKey a = new FlightKey(temp[0], temp[1], temp[2], temp[3]);
				// elements 5-6 = FlightData
				FlightData b = new FlightData(temp[4],
						Double.parseDouble(temp[5]));
				insert(a, b);
			}
		} catch (Exception e) {
			System.out.println("Sorry, unable to create flight list.");
		}
	}

	// Finds the level of a given FlightNode
	public int level(FlightNode node) {
		int count = 1;
		FlightNode temp = node;
		while (temp.down() != null) {
			count += 1;
		}
		return count;
	}

	//Insert a (key, value) pair to the skip list. The key is of type FlightKey 
	//and stores origin, destination, date and time as strings. 
	//Return true if it was able to successfully insert the element. 
	public boolean insert(FlightKey key, FlightData data) {

		// calls find to see if the node is already in. if in, return true.
		// if not in, do the following
		if (find(key) == true) {
			return false;
		} else {
			// create new node
			FlightNode newNode = new FlightNode(key, data);
			// decide how many levels the new node has
			int newLevel = randomLevel();
			// create the new Node with newLevel
			FlightNode newTemp = newNode;
			for (int i = 0; i < newLevel - 1; i++) {
				newTemp.setUp(new FlightNode(key, data));
				newTemp.up().setDown(newTemp);
				newTemp = newTemp.up();
			}
			// 2 cases now:
			if (newLevel >= numLevel) {
				int levelsAdd = (newLevel - numLevel) + 1;
				for (int i = 0; i < levelsAdd; i++) {
					// System.out.println("Adding " + i );
					start.setUp(new FlightNode(null, null));
					start.up().setDown(start);
					start = start.up();
					start.setNext(new FlightNode(null, null));
					start.next().setDown(start.down().next());
					start.down().next().setUp(start.next());
				}
				numLevel = newLevel + 1;
			}

			FlightNode[] pointers = new FlightNode[numLevel];
			// find where the number should be added
			FlightNode current = start;
			int index = 0;
			// for (index = 0; index < numLevel; index++) {
			// while not at end of list
			while (index < numLevel) {
				while (current.next().next() != null
						&& current.next().getKey() != null
						&& current.next().getKey().compareTo(key) < 0) {
					current = current.next();
				}
				pointers[index] = current;
				current = current.down();
				index += 1;
			}
			// add node with now filled array
			FlightNode newNodeTemp = newNode;
			for (int j = numLevel - 1; j > (numLevel - (newLevel + 1)); j--) {
				// going from end of list to beginning
				FlightNode tempNext = pointers[j].next();
				pointers[j].setNext(newNodeTemp);
				newNodeTemp.setPrevious(pointers[j]);
				newNodeTemp.setNext(tempNext);
				tempNext.setPrevious(newNodeTemp);
				newNodeTemp = newNodeTemp.up();
			}
			return true;
		}

	}

	//Returns true if it was able to find the entry with a given key in the skip list. This search has to be efficient
	public boolean find(FlightKey key) {
				
		FlightNode current = start;
		
		while (true){
			if(current.next().getKey() == null){
				if(current.down() == null){
					return false;
				}
				current = current.down();
			}else if (current.next().getKey().compareTo(key) == 0){
				return true;
			}
			else if(current.next().getKey().compareTo(key) > 0){
				if(current.down() == null){
					return false;
				}
				current = current.down();
			}else{
				current = current.next();
			}
		}

	}

	private ArrayList<FlightNode> matching(FlightKey key) {

		// array of matchings unsorted
		ArrayList<FlightNode> unsorted = new ArrayList<FlightNode>();

		// point current to root level
		FlightNode current = start;
		while (current.down() != null) {
			current = current.down();
		}

		while (current.next().getKey() != null) {
			current = current.next();
			if (current.getKey().getOrigin().equals(key.getOrigin())
					&& current.getKey().getDest().equals(key.getDest())
					&& current.getKey().getDate().equals(key.getDate())) {
				unsorted.add(current);
			}
		}

		ArrayList<FlightNode> sorted = new ArrayList<FlightNode>();

		for (int i = unsorted.size() - 1; i >= 0; i--) {
			FlightNode max = unsorted.get(0);
			for (int j = 0; j <= i; j++) {
				if (unsorted.get(j).getKey().getTime()
						.compareTo(max.getKey().getTime()) < 0) {
					max = unsorted.get(j);
				}
			}
			sorted.add(max);
			unsorted.remove(unsorted.get(0));
		}
		return sorted;
	}

	//Returns a list of nodes that have the same origin and destination cities 
	//and the same date as the key, with departure times in increasing order from the requested departure time.
	public ArrayList<FlightNode> successors(FlightKey key) {

		ArrayList<FlightNode> sorted = matching(key);
		ArrayList<FlightNode> toReturn = new ArrayList<>();
		for(int i=0; i<sorted.size(); i++){
		
			if (sorted.get(i).getKey().getTime().compareTo(key.getTime()) > 0){
				toReturn.add(sorted.get(i));
			}
		}
		return toReturn;

	}

	//Returns a list of nodes that have the same origin and destination cities and 
	//the same date as the key, with departure times in decreasing order from the requested departure time.
	public ArrayList<FlightNode> predecessors(FlightKey key) {

		ArrayList<FlightNode> sorted = matching(key);
		ArrayList<FlightNode> toReturn = new ArrayList<>();
		for(int i=0; i<sorted.size(); i++){
		
			if (sorted.get(i).getKey().getTime().compareTo(key.getTime()) < 0){
				toReturn.add(sorted.get(i));
			}
		}
		return toReturn;
	}

	//Returns a list of nodes that have the same origin and destination cities and 
	//the same date as the key, with departure times within the timeDifference of the departure time of the key.
	public ArrayList<FlightNode> findFlights(FlightKey key, int timeDifference) {

		ArrayList<FlightNode> sorted = matching(key);
		ArrayList<FlightNode> toReturn = new ArrayList<FlightNode>();

		for (int i = 0; i < sorted.size(); i++) {
			int a = Integer.parseInt(sorted.get(i).getKey().getTime()
					.substring(0, 2));
			int b = Integer.parseInt(key.getTime().substring(0, 2));
			int difference = a - b;

			if (Math.abs(difference) <= timeDifference){
				toReturn.add(sorted.get(i));
			}
		}
		return toReturn;
	}

	//Prints the elements of the skip list on all the levels starting from the topmost level.
	public void print() {

		FlightNode tempHeads = start;
		FlightNode current = tempHeads;
		System.out.println("\nNEXT LEVEL");
		while (tempHeads.down() != null) {
			System.out.println("\nNEXT LEVEL");
			tempHeads = tempHeads.down();
			current = tempHeads;
			while (current.next().next() != null) {
				System.out.println(current.next().getKey().toString());
				current = current.next();
			}

		}
		System.out.println("\nEnd of List.");
	}

}
