import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class creates a search result database. It parses through a given file,
 * it calls search method from InvertedIndex class that searches through an
 * inverted index for words that starts with the query words. Then it will
 * create LinkedHasmap that stores all the search results from given query
 * words. There's also a write-to-file method that writes the results to a given
 * text file.
 * 
 * @author Musica Zheng
 */

public class QueryParser {

	private static final Logger logger = LogManager.getLogger(QueryParser.class
			.getName());

	private LinkedHashMap<String, ArrayList<SearchResult>> searchResults;

	private final WorkQueue workers;
	private final MultiReaderLock lock;
	private int pending;
	private InvertedIndex index;

	// Constructor
	public QueryParser(InvertedIndex index, int numThreads) {
		searchResults = new LinkedHashMap<>();
		workers = new WorkQueue(numThreads);
		lock = new MultiReaderLock();
		pending = 0;
		this.index = index;
	}

	/**
	 * Parses through the query file line by line. Then, it calls the search
	 * method from the InvertedIndex class to get arraylist of matching sorted
	 * results. Then, it adds the results in to a the LinkedHashMap,
	 * searchResults.
	 * 
	 * @param index
	 *            to search through
	 * @param queryFile
	 *            to parse
	 */
	public void queryParser(String queryFile) {
		File file = new File(queryFile);
		Path path = file.toPath();
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				lock.lockWrite();
				searchResults.put(line, null);
				lock.unlockWrite();
				workers.execute(new SearchWorker(line));
			}
			finish();
		} catch (Exception e) {
			logger.warn("Unable to parse {}", queryFile);
			logger.catching(Level.DEBUG, e);
		}
	}

	/**
	 * Search worker for queryParser
	 *
	 */
	private class SearchWorker implements Runnable {

		private String line;

		public SearchWorker(String line) {

			//logger.debug("Worker created for query word(s)= {}", line);
			this.line = line;

			incrementPending();
		}

		public void run() {
			try {

				ArrayList<SearchResult> temp = index.search(line.split("\\s"));
				lock.lockWrite();
				searchResults.put(line, temp);
				lock.unlockWrite();
				decrementPending();

			} catch (Exception e) {
				logger.warn("Unable to search for {}", line);
				logger.catching(Level.DEBUG, e);
				decrementPending();
			}

		}
	}

	private synchronized void incrementPending() {
		pending++;
	}

	private synchronized void decrementPending() {
		pending--;

		if (pending <= 0) {
			this.notifyAll();
		}
	}


	/**
	 * Makes sure if any work is still going on including traversing and writing
	 * on tree set
	 */
	public synchronized void finish() {
		try {
			while (pending > 0) {
				this.wait();
			}
		} catch (InterruptedException e) {
			logger.debug("Finish interrupted", e);
		}
	}

	/**
	 * Shuts down workers
	 */
	public void shutdown() {
		//logger.debug("Shutting down");
		finish();
		workers.shutdown();
	}

	/**
	 * Writes results from the LinkedHashMap to the provided output file.
	 * 
	 * @param outputFile
	 *            to write results in
	 */
	public void writeSearchToFile(Path outputFile) {
		Charset charset = Charset.forName("UTF-8");
		lock.lockRead();
		try (BufferedWriter writer = Files.newBufferedWriter(outputFile,
				charset)) {
			Set<String> qWords = searchResults.keySet();
			for (String word : qWords) {
				writer.write(word);
				writer.newLine();
				ArrayList<SearchResult> results = searchResults.get(word);
				for (SearchResult result : results) {
					writer.write(result.toString());
				}
				writer.newLine();
			}
		} catch (Exception e) {
			logger.warn("Unable to write searchResults to {}", outputFile);
			logger.catching(Level.DEBUG, e);
		}
		lock.unlockRead();
	}

}
