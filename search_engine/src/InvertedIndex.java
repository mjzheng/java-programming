import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that handles creating a new database, adding words onto the database,
 * and using a BufferedWriter to write the database onto a txtfile.
 * 
 * @author Musica Zheng
 */
public class InvertedIndex {

	private static final Logger logger = LogManager
			.getLogger(InvertedIndex.class.getName());

	// Stores database
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	private final MultiReaderLock lock;

	/**
	 * Constructor for an InvertedIndex that just creates a new index.
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<>();
		lock = new MultiReaderLock();

	}

	/**
	 * Adds a word to invertedIndex. If the word already exists, this method
	 * will only add another path and location of the existing word. If the word
	 * already exists and the path of where the word is from also already is in
	 * the index already,a new location of where the word is will be added. If
	 * the word doesn't exist, this method will create a new word in the index
	 * and add it's path and location along with it.
	 * 
	 * @param key
	 *            to add to invertedIndex
	 * @param filePath
	 *            to add to invertedIndex
	 * @param location
	 *            to add to invertedIndex
	 */
	public void addWord(String key, String filePath, int location) {
		logger.debug("Adding {} to index", key);

		lock.lockWrite();

		TreeSet<Integer> newLocation = new TreeSet<>();
		// TreeSet<Integer> location
		if (invertedIndex.containsKey(key)) {
			// if same word, same path, but different location
			// add location
			if (invertedIndex.get(key).containsKey(filePath)) {
				invertedIndex.get(key).get(filePath).add(location);
			}
			// else, add new file path and location
			else {
				newLocation.add(location);
				invertedIndex.get(key).put(filePath, newLocation);
			}
		} else {
			// creates a new object in TreeMap
			TreeMap<String, TreeSet<Integer>> add = new TreeMap<>();
			newLocation.add(location);
			add.put(filePath, newLocation);
			invertedIndex.put(key, add);
		}

		lock.unlockWrite();

	}

	/**
	 * Add all the words from the other inverted index to this inverted index.
	 * For each key words in the other inverted index, 
	 * if this inverted index doesn't contain this key word,
	 * this inverted index will add the word and value from other index.
	 * if this inverted index contains the key word, it will add a new path 
	 * and locations to it's existing treeMap 
	 * 
	 * @param other
	 *             to add to this.invertedIndex
	 */
	public void addAll(InvertedIndex other) {
		// for each key words in the local index
		lock.lockWrite();
		for (String key : other.invertedIndex.keySet()) {

			if (!this.invertedIndex.containsKey(key)) {
				this.invertedIndex.put(key, other.invertedIndex.get(key));
			}

			else {

				TreeMap<String, TreeSet<Integer>> temp = this.invertedIndex.get(key);
				
				for (String location : other.invertedIndex.get(key).keySet()) {
					
					if (this.invertedIndex.containsKey(location)){
						this.invertedIndex.get(key).get(location).addAll(other.invertedIndex.get(key).get(location));

					}
					
					// TODO Test if the file exists in this.invertedIndex, and if
					// not, just call put() on the index. Otherwise, use addAll()
					else{
						temp.put(location, other.invertedIndex.get(key).get(location));
					}
					

				}
				this.invertedIndex.put(key, temp);

			}
		}
		lock.unlockWrite();
	}

	/**
	 * Uses new BufferedWriter to open output file and writes the invertedIndex
	 * database onto the file. It obtains all the different words, and for each
	 * word, it writes each of it's path and location on to the output file. If
	 * it fails to write, a friendly message will be printed and program exits.
	 * 
	 * @param output
	 *            to write database in
	 */
	public void writeFile(Path output) {
		Charset charset = Charset.forName("UTF-8");
		lock.lockRead();
		try (BufferedWriter writer = Files.newBufferedWriter(output, charset)) {
			Set<String> keys = invertedIndex.keySet();
			for (String key : keys) {
				writer.write(key + "\n");
				Set<String> paths = invertedIndex.get(key).keySet();
				for (String path : paths) {
					writer.write("\"" + path.toString() + "\"");
					Set<Integer> locations = invertedIndex.get(key).get(path);
					for (Integer location : locations) {
						writer.write(", " + location);
					}
					writer.write("\n");
				}
				writer.write("\n");
			}
		} catch (Exception e) {
			logger.warn("Unable to write inverted index to {}", output);
			logger.catching(Level.DEBUG, e);
			System.out
					.println("Sorry, the file cannot be written succesfully.");
		}
		lock.unlockRead();
	}

	/**
	 * Searches through the inverted index for word that starts with the given
	 * query words.
	 * 
	 * @param queryWords
	 * @return a sorted resultlist of all the matching results from the inverted
	 *         index
	 */
	public ArrayList<SearchResult> search(String[] queryWords) {
		lock.lockRead();

		// Stores location, and associated SearchResult object
		HashMap<String, SearchResult> resultMap = new HashMap<>();

		// for each query word
		for (String word : queryWords) {

			// for each appropriate key in index
			for (String key : invertedIndex.tailMap(word).keySet()) {

				if (key.startsWith(word)) {
					for (String location : invertedIndex.get(key).keySet()) {
						int frequency = invertedIndex.get(key).get(location)
								.size();
						int initialPosition = invertedIndex.get(key)
								.get(location).first();

						// if location is in resultMap, update result
						if (resultMap.containsKey(location)) {
							resultMap.get(location).updateResult(frequency,
									initialPosition);
						}

						else {
							// create new SearchResult object
							resultMap.put(location, new SearchResult(location,
									frequency, initialPosition));
						}
					}
				} else if (!key.startsWith(word)) {
					break;
				}
			}
		}

		lock.unlockRead();
		ArrayList<SearchResult> resultList = new ArrayList<>(resultMap.values());
		Collections.sort(resultList);
		return resultList;
	}

}