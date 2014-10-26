import java.nio.file.Path;
import java.nio.file.Paths;

// TODO Everything should still work even with logging to debug.log

/**
 * Driver class that calls different classes and methods to create a database.
 * It will first make sure a valid directory is provided in order to create a
 * database, else, the program will just exit. Next, if a directory is correctly
 * provided, it will call the constructor of InvertedIndex to create an empty
 * index. Then, it will call the traverseDirectory method to create a TreeMap
 * database. Lastly, it will call the method writeFile to output the database on
 * to a .txt file.
 * 
 * @author Musica Zheng
 */

public class Driver {

	public static void main(String[] args) {
		
		String tag;
		String dirOrUrl;
		Path indexOutputFile;

		String queriesPath;
		Path queriesSearchOutputFile;

		int numThreads;

		// checks if there's args or not
		if (args.length == 0) {
			return;
		} else {
			tag = null;
			
			dirOrUrl = null;
			indexOutputFile = null;

			queriesPath = null;
			queriesSearchOutputFile = null;
			numThreads = 5;
		}

		// create flag/value pairs for directory and output file name
		ArgumentParser arguments = new ArgumentParser(args);

		if (arguments.hasFlag("-t")) {
			// tests if it's not float or a word
			try {
				// test if the -t flag value is valid or not, if not, use
				// default value
				// tests if it's not 0 nor a negative number
				if (Integer.parseInt(arguments.getValue("-t")) > 0) {
					numThreads = Integer.parseInt(arguments.getValue("-t"));
				}
			} catch (Exception e) {
				return;
			}

		}

		// if a directory is given, set input as given directory
		// else, program exits
		if (arguments.hasFlag("-d")) {
			tag = "-d";
			dirOrUrl = arguments.getValue("-d");
		} else if(arguments.hasFlag("-u")){
			tag = "-u";
			dirOrUrl = arguments.getValue("-u");
		}else {
			System.out.println("Hello, no directory or URL was given.");
			return;
		}

		// If output file name is provide, change default output to provided
		// output file.
		if (arguments.hasFlag("-i") && arguments.getValue("-i") != null) {
			indexOutputFile = Paths.get(arguments.getValue("-i"));
		} else if ((!arguments.hasFlag("-i") && !arguments.hasFlag("-q"))
				|| (arguments.hasFlag("-i") && arguments.getValue("-i") == null)) {
			indexOutputFile = Paths.get("invertedindex.txt");
		} else if (!arguments.hasFlag("-i") && arguments.hasFlag("-q")) {
			indexOutputFile = null;
		}

		if (arguments.hasFlag("-q") && arguments.getValue("-q") != null) {
			queriesPath = arguments.getValue("-q");
		}

		if (arguments.hasFlag("-r") && arguments.getValue("-r") != null
				&& (arguments.hasFlag("-q"))) {
			queriesSearchOutputFile = Paths.get(arguments.getValue("-r"));
		} else if (arguments.hasFlag("-r") && arguments.getValue("-r") == null) {
			queriesSearchOutputFile = Paths.get("searchresults.txt");
		}

		if ((!arguments.hasFlag("-q")) && arguments.hasFlag("-r")) {
			System.out.println("-r flag is invalid without the -q flag");
		}

		if (dirOrUrl != null) {
			InvertedIndex index = new InvertedIndex();
			IndexBuilder build = new IndexBuilder(numThreads);
			build.traverseDirectory(tag, dirOrUrl, index);
			build.shutdown();
			
			if (indexOutputFile != null) {
				index.writeFile(indexOutputFile);
			}

			if (queriesPath != null) {
				QueryParser searchResults = new QueryParser(index, numThreads);
				searchResults.queryParser(queriesPath);
				searchResults.shutdown();

				if (queriesSearchOutputFile != null) {
					searchResults.writeSearchToFile(queriesSearchOutputFile);
				}
			}
		}
		return;
	}
}