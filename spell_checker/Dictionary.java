import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * 
 * @author Musicajzheng
 * 
 */

public class Dictionary {

	public ArrayList<String> allWords;

	/**
	 * Node class
	 */
	public static class Node {

		private boolean valid;
		private Node[] children;
		private String prefix;

		public Node(String prefix) {
			this.prefix = prefix;
			children = new Node[26];
			valid = false;
		}

	}

	public Node root;

	/**
	 * Constructors
	 */

	/**
	 * Create an empty dictionary
	 */
	public Dictionary() {
		root = new Node("");
	}

	/**
	 * Create a dictionary from a file
	 */
	public Dictionary(String filename) {
		root = new Node("");
		Charset charset = Charset.forName("UTF-8");
		File file = new File(filename);
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(),
				charset)) {
			String line = null;
			// while it's not end of file
			while ((line = reader.readLine()) != null) {

				String word = line;

				add(word);
			}
		} catch (Exception e) {
			System.out.println("Sorry, unable to read file.");
		}

	}

	// helper method to get the suffix of a word
	public String getSuffix(String word, String prefix) {

		int length = prefix.length();

		String suffix = word.substring(length);

		return suffix;
	}

	// find the common prefix of two words
	public String getCommonPrefix(String word1, String word2) {
		String newPrefix = "";
		char[] w1 = word1.toCharArray();
		char[] w2 = word2.toCharArray();
		for (int i = 0; i < word1.length(); i++) {
			if (w1[i] == (w2[i])) {
				newPrefix += w1[i];
			} else {
				break;
			}
		}
		return newPrefix;
	}

	/**
	 * Add a word into the dictionary
	 */
	public void add(String word) {

		root = add(word, root);
	}

	public Node add(String word, Node tree) {

		// An empty tree: Create a new node whose prefix is the word you are
		// looking for,
		// and whose valid bit is true. Return this node
		if (tree == null) {
			Node temp = new Node(word);
			temp.valid = true;
			return temp;
		}
		// A node whose prefix is the same as the word you are looking for,
		// with the valid bit set to false. Set this bit to true, and return the
		// tree
		else if (tree.prefix.equals(word) && tree.valid == false) {
			tree.valid = true;
			return tree;
		} else if (tree.prefix.equals(word) && tree.valid == true) {
			return tree;
		}
		// if word does not start with prefix
		else if (!word.startsWith(tree.prefix)) {

			Node newPrefix = new Node(getCommonPrefix(word, tree.prefix));
			// suffix of the original prefix
			String suffix = getSuffix(tree.prefix, newPrefix.prefix);
			// suffix of the word you are adding
			String suffixWord = getSuffix(word, newPrefix.prefix);
			// set prefix of original tree to suffix
			tree.prefix = suffix;
			// set the child of the new node corresponding to the first letter
			// of suffix to the original tree
			int index = getIndex(suffix.charAt(0));
			newPrefix.children[index] = tree;
			// Recursively insert suffixWord into the appropriate child of the
			// new node
			int index2 = getIndex(suffixWord.charAt(0));
			newPrefix.children[index2] = add(suffixWord,
					newPrefix.children[index2]);
			// return the new node
			return newPrefix;
		}
		// recursively go down the tree
		else {

			String suffix = getSuffix(word, tree.prefix);
			int index = getIndex(suffix.charAt(0));
			tree.children[index] = add(suffix, tree.children[index]);
			return tree;
		}
	}

	/**
	 * Checks to see if a word is in the dictionary
	 */
	public boolean check(String word) {
		String wordTemp = word.toLowerCase();
		return check(root, wordTemp);
	}

	private boolean check(Node tree, String word) {

		if (tree == null) {
			// System.out.println("1");
			return false;
		} else if (word.startsWith(tree.prefix) == false) {
			// System.out.println("2");
			return false;
		} else if (tree.prefix.equals(word) && tree.valid == false) {
			// System.out.println("3");
			return false;
		} else if (tree.prefix.equals(word) && tree.valid == true) {
			// System.out.println("4");
			return true;
		} else {
			String suffix = getSuffix(word, tree.prefix);
			int index = getIndex(suffix.charAt(0));

			return check(tree.children[index], suffix);
		}
	}

	/**
	 * Checks to see if a prefix matches a word in the dictionary
	 */
	public boolean checkPrefix(String prefix) {
		return checkPrefix(root, prefix);
	}

	private boolean checkPrefix(Node tree, String prefix) {
		// if tree is empty
		if (tree == null) {
			return false;
		}
		// if root prefix starts with prefix
		else if (tree.prefix.startsWith(prefix) == true) {
			return true;
		}
		// if prefix doesn't start with root prefix
		else if (prefix.startsWith(tree.prefix) == false) {
			return false;
		} else {

			int length = tree.prefix.length();
			String suffix = prefix.substring(length);
			int index = getIndex(suffix.charAt(0));
			return checkPrefix(tree.children[index], suffix);
		}
	}

	/**
	 * Print out the contents of the dictionary, in alphabetical order, one word
	 * per line.
	 */
	public void print() {
		for (int i = 0; i < 26; i++) {
			if (root.children[i] != null) {
				print(root.children[i], "");
			}
		}

	}

	private void print(Node tree, String newWord) {

		if (tree != null) {
			newWord += tree.prefix;
			if (tree.valid == true) {
				System.out.println(newWord);
			}
		}
		for (int i = 0; i < 26; i++) {
			if (tree.children[i] != null) {
				print(tree.children[i], newWord);

			}
		}
	}

	/**
	 * Print out the tree structure of the dictionary, in a pre-order fashion.
	 */
	public void printTree() {
		for (int i = 0; i < 26; i++) {
			if (root.children[i] != null) {
				printTree(root.children[i], 0);
			}
		}
	}

	private void printTree(Node tree, int offset) {
		if (tree != null) {
			for (int j = 0; j < offset; j++) {
				System.out.print(" ");
			}
			System.out.print(tree.prefix);
			if (tree.valid == true) {
				System.out.print("<T>");
			}
			System.out.println();
		}
		for (int i = 0; i < 26; i++) {

			if (tree.children[i] != null) {
				printTree(tree.children[i], offset + 2);
			}
		}
	}

	/**
	 * Return an array of the entries in the dictionary that are as close as
	 * possible to the parameter word.
	 */
	public String[] suggest(String word, int numSuggestions) {
		return suggest(root, word, numSuggestions);
	}

	private String[] suggest(Node tree, String word, int numSuggestions) {
		collectWords(tree);

		// if word is in there
		if (check(word) == true) {
			String[] oneWord = new String[] { word };
			return oneWord;
		} else {
			String[] suggestWords = new String[numSuggestions];
			int count = 0;
			for (String temp : allWords) {
				if (count < numSuggestions
						&& (temp.contains(word) || word.contains(temp))) {
					suggestWords[count] = temp;
					count += 1;
				} else if (count >= numSuggestions) {
					break;
				}
			}
			// if there's null space in suggest words, then add again with less
			// matching words

			for (String temp2 : allWords) {
				if (count < numSuggestions
						&& temp2.startsWith(Character.toString(word.charAt(0)))) {
					suggestWords[count] = temp2;
					count += 1;
				} else if (count >= numSuggestions) {
					break;
				}
			}

			return suggestWords;
		}
	}

	// helper for suggest()
	public void collectWords(Node tree) {
		allWords = new ArrayList<String>();
		for (int i = 0; i < 26; i++) {
			if (tree.children[i] != null) {
				collectWords(tree.children[i], "");
			}
		}

	}

	// helper for collectWords()
	private void collectWords(Node tree, String newWord) {

		if (tree != null) {
			newWord += tree.prefix;
			if (tree.valid == true) {
				allWords.add(newWord);
			}
		}
		for (int i = 0; i < 26; i++) {
			if (tree.children[i] != null) {
				collectWords(tree.children[i], newWord);
			}
		}
	}

	public int getIndex(char letter) {
		char ch = letter;
		int index = (int) ch - 97;
		return index;
	}

}
