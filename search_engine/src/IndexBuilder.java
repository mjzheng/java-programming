import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO Add Javadoc

/**
 * 
 * Class that handles traversing through a directory and parsing a file line by
 * line. This is a helper class for the class, InvertedIndex.
 * 
 * 
 * @author Musica Zheng
 * 
 */

public class IndexBuilder {

	private static final Logger logger = LogManager
			.getLogger(IndexBuilder.class.getName());

	private final WorkQueue workers;
	private int pending;
	private ArrayList<String> uniqueLinks; // TODO final, do not use a list and .contains, use a set instead
	//private final MultiReaderLock lock;

	// Constructor
	public IndexBuilder(int numThreads) {
		pending = 0;
		workers = new WorkQueue(numThreads);
		//lock = new MultiReaderLock();
		uniqueLinks = new ArrayList<>();
	}

	/**
	 * 
	 * Recursively traverses through the given root directory to check if it's a
	 * directory or file. If it's a directory, it will continue to check if it's
	 * a directory or file. If it's a file, it will call the parseFile method,
	 * giving the file's path and index as parameters, to parse the file.
	 * 
	 * @param root
	 *            to traverse
	 * @param index
	 *            to provide parseFile method's parameter
	 */

	public void traverseDirectory(String tag, String input, InvertedIndex index) {

		try {
			if (tag.equals("-d")) {
				if (Files.isDirectory(Paths.get(input))) {
					workers.execute(new DirectoryWorker(Paths.get(input), index));
				}
			} else if(tag.equals("-u")){
				uniqueLinks.add(input); // TODO protect modification to uniqueLinks
				workers.execute(new UrlWorker(input, index));
			}
		} catch (Exception e) {
			logger.warn("Unable to find files for {}", input);
			logger.catching(Level.DEBUG, e);
		}

	}

	private class DirectoryWorker implements Runnable {

		private Path directory;
		private InvertedIndex index;

		public DirectoryWorker(Path directory, InvertedIndex index) {
			logger.debug("Worker created for {}", directory);
			this.directory = directory;
			this.index = index;

			incrementPending();
		}

		@Override
		public void run() {
			try {

				for (Path path : Files.newDirectoryStream(directory)) {
					if (Files.isDirectory(path)) {

						workers.execute(new DirectoryWorker(path, index));
					} else {
						// if it's a text file, parse it.
						if ((path.toString().toLowerCase()).endsWith(".txt")) {
							// for each file, a new local index is created ,
							// therefore, each path is the
							InvertedIndex local = new InvertedIndex();
							parseFile(path, local);
							index.addAll(local);
						}
					}
				}
			} catch (IOException e) {
				logger.warn("Unable to parse {}", directory);
				logger.catching(Level.DEBUG, e);
			}
			decrementPending();
	   }
	}


	/**
	 * 
	 * Reads the file with BufferedReader and parses it line by line. Normalizes
	 * all the words before calling the method, addWord, from InvertedIndex
	 * class, to add word to a database. It also counts and keep track of the
	 * location of each word to put in the database. This method provides each
	 * new word, the file the word was from, and the location of the word from
	 * the file, to the parameter of addWord method.
	 * 
	 * @param file
	 *            to parse
	 * @param index
	 *            to add word
	 */
	public void parseFile(Path file, InvertedIndex local) {
		int count = 0;
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] tempLine = line.toLowerCase().split(" ");
				for (String tempWord : tempLine) {
					if ((!tempWord.isEmpty())
							&& (!tempWord.replaceAll("[\\W_]", "").isEmpty())) {
						count += 1;
						local.addWord(tempWord.replaceAll("[\\W_]", ""), file
								.toAbsolutePath().toString(), count);
					}
				}
			}
		} catch (Exception ex) {
			logger.warn("Unable to parse file for {}", file);
			logger.catching(Level.DEBUG, ex);
		}

	}

	private class UrlWorker implements Runnable {

		private String url;
		private InvertedIndex index;

		public UrlWorker(String url, InvertedIndex index) {
			this.url = url;
			this.index = index;
			
			incrementPending();

		}

		@Override
		public void run() {
			try {
				// TODO Fetch once, get the links and words from the HTML that you fetched
				
				// assume link works at this piont
				ArrayList<String> words = HTMLCleaner.fetchWords(url);
				InvertedIndex local = new InvertedIndex();
				addHTMLWords(url, words, local);
				index.addAll(local);
				
				String html = HTMLCleaner.fetchHTML(url);
				String cleanedHTML = HTMLCleaner.stripElement("style",HTMLCleaner.stripElement("script", html));
				ArrayList<String> links = HTMLLinkParser.listLinks(cleanedHTML);
				//System.out.println(links);

				URL base = new URL(url);
				
				// TODO Lock for write around this whole for loop
				for (String link: links){
					URL absolute = new URL(base, link);
					// if uniqueLinks < 50 && link != base
					if (uniqueLinks.size() < 50 && !base.sameFile(absolute) && !uniqueLinks.contains(absolute.toString())){
						// for each uniqueLinks, if link != each, then add to uniqueLinks
						uniqueLinks.add(absolute.toString());
						workers.execute(new UrlWorker(absolute.toString(), index));
					}
				}

				} catch (Exception e) {
					logger.warn("Unable to find files for {}", url);
					logger.catching(Level.DEBUG, e);
					//e.printStackTrace();
			}
			decrementPending();
		}
	}
	
	// TODO Instead of words, pass in cleaned HTML, or clean the HTML in this method and parse into words
	public void addHTMLWords(String url, ArrayList<String> words, InvertedIndex local){
		int count = 0;
		// for every word
		for (String word: words){
		    count += 1;
			// add it to local along with its location and position
		    local.addWord(word, url, count);
		}
	}

	private synchronized void incrementPending() {
		pending++;
		// logger.debug("Pending is now {}", pending);
	}

	/**
	 * Keeps track on
	 */
	private synchronized void decrementPending() {
		pending--;
		// logger.debug("Pending is now {}", pending);

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
	 * Shuts down workers threads
	 */
	public void shutdown() {
		// logger.debug("Shutting down");
		finish();
		workers.shutdown();
	}

}
