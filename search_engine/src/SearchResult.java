/**
 * This class stores a search result object for a single search result. It
 * contains a method where it decides of it should update the initial position
 * and combines the frequency of mutiple word queries if they are from the same
 * locations.
 * 
 * @author Musica Zheng
 * 
 */
public class SearchResult implements Comparable<SearchResult> {

	private final String location;
	private int frequency;
	private int initialPosition;

	// constructor
	public SearchResult(String location, int frequency, int position) {
		this.location = location;
		this.frequency = frequency;
		this.initialPosition = position;
	}

	/**
	 * Calls both updateFrequency method and updatePosition methods to update
	 * result.
	 * 
	 * @param frequency1
	 *            to update frequency
	 * @param position1
	 *            to update initial position
	 */
	public void updateResult(int frequency1, int position1) {
		updateFrequency(frequency1);
		updatePosition(position1);
	}

	/**
	 * Adds the two frequency together
	 * 
	 * @param frequency2
	 *            to update frequency
	 */
	private void updateFrequency(int frequency2) {
		frequency += frequency2;
	}

	/**
	 * Checks for the smallest position value. If position2 is less than
	 * initialPosition, the new initialPosition will be replaced by the value of
	 * position2.
	 * 
	 * @param position2
	 *            to update initial position
	 */
	private void updatePosition(int position2) {
		if (position2 < initialPosition) {
			initialPosition = position2;
		}
	}

	/**
	 * Sorts SearchResult objects.
	 * 
	 * @param other
	 *            to compare
	 */
	@Override
	public int compareTo(SearchResult other) {
		if (this.frequency > other.frequency) {
			return -1;
		} else if (this.frequency == other.frequency) {
			if (this.initialPosition < other.initialPosition) {
				return -1;
			} else if (this.initialPosition == other.initialPosition) {

				return this.location.compareToIgnoreCase(other.location);

			} else {
				return 1;
			}
		}
		return 1;

	}

	/**
	 * Returns result as a string.
	 * 
	 * @return result as a string
	 */
	@Override
	public String toString() {
		return "\"" + location + "\", " + Integer.toString(frequency) + ", "
				+ Integer.toString(initialPosition) + "\n";
	}

}
