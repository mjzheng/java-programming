package usflix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Driver class manages user interaction: input and output between the program
 * and users
 */
public class Driver {
	
	// NOTE THAT SVN MESSES UP MY INDENTATION FOR NO REASON
	// PLEASE DON'T MARK ME OFF PIONTS FOR WEIRD INDENTATIONS. 
	// STILL DID NOT GET THIS PROBLEM FIXED YET

	/**
	 * There will be only one movie database and one user database in the system,
	 * so they are declared as static.
	 */
	
	// make movieDB and userDB instance variables
	private static MovieDatabase movieDB;
	private static UserDatabase userDB;
	private static ArrayList <String> usernames;
	private static ArrayList <String> movieNames;
	private static double[][] ratingsDB;
	
	// make class variables for ratings
	protected static final Float maxRating = (float) 5; 
	protected static final Float minRating = (float) 0.5;

	/**
	 * The first program argument (args[0]) may contain the movie information
	 * file name, and the second program argument (args[1]) may contain the user
	 * information file name.
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		

		Scanner scan = new Scanner(System.in);    
		
        // creates new UserDatabase
		userDB = new UserDatabase();
		usernames = new ArrayList <String> ();
		movieNames = new ArrayList <String> ();

		// if args has two variables
		if (args.length > 1) {
			// creates new movie database with movie database file
			movieDB = new MovieDatabase(args[0]);
			// tries to load the user file
			try {
				loadUsers(args[1]);
				
			} catch (FileNotFoundException e) {
				System.out.println("User file not found");
			}
		} else if (args.length > 0) {
			movieDB = new MovieDatabase(args[0]);
		} else {
			// manually type in name if name was not given in arguments
			System.out.print("Please enter the movie database file name: ");
			movieDB = new MovieDatabase(scan.nextLine());
		}

		// gives the user options to choice what they want to do
		int choice = -1;
		while (choice != 0) {
			try {
				System.out
						.println("Welcome to USFlix! Select an option from the menu.");
				System.out
						.println("1 to load users and their ratings from a file");
				System.out.println("2 to login");
				System.out.println("3 to create a new account");
				System.out.println("0 to quit");
				System.out.print("Enter your choice: ");
				choice = Integer.parseInt(scan.nextLine());

				switch (choice) {
				case 0:
					System.out.println("Bye!");
					break;
				case 1:
					// askes user to enter user file's name and loads it using the LoadUsers method
					System.out.print("Enter the file name: ");
					loadUsers(scan.nextLine());
					break;
				case 2:
					// Asks user for username and password
						System.out.print("Enter the username: ");
						String username1 = scan.nextLine();
						System.out.print("Enter the password: ");
						String password1 = scan.nextLine();
						User u = userDB.login(username1, password1);
						if (u == null){
							System.out.println("Login error");
						}else {
							userMenu(u);
							break;
						}
					
					
				case 3:
					// creating new account
					// repeats itself until successfully creates account
					boolean created = false;
					while (created == false){
						System.out.print("Enter your first name: ");
						String firstName = scan.nextLine();
						System.out.print("Enter your last name: ");
						String lastName = scan.nextLine();
						System.out.print("Enter a username: ");
						String username = scan.nextLine();
						System.out.print("Enter a password: ");
						String password = scan.nextLine();
						//TODO: account creation
						User newUser = userDB.createAccount(firstName, lastName, username, password);
						if (newUser != null){
							created = true;
							usernames.add(username);
							break;
						} else {
							System.out.println("Username or Password is in valid. Please try again.");
						}
					}
				}

			} catch (InputMismatchException e) {
				System.out.println("Invalid choice");
			} catch (FileNotFoundException e) {
				System.out.println("User file is not found");
			}
		}
	}
	
	/*private static ArrayList<String> MovieNames(ArrayList<String> usernames){
		ArrayList<String> moviesWatched = new ArrayList<String> ();
		for (int i=0; i<usernames.size(); i++){
			String user = usernames.get(i);
			ArrayList<Movie> list = userDB.seenMoviesOtherUsers(user);
			for (int j=0; j<list.size(); j++){
				String title = list.get(j).title;
				if (!moviesWatched.contains(title)){
					moviesWatched.add(title);
				}
			}
		}
		return moviesWatched;
	}*/

	/**
	 * loadUsers() method loads the user information from a file. If the username
	 * is available, then the new account is created and his or her rating information 
	 * gets added to the movie database and also to the user object. If the username
	 * is not available, then this method skips to the next user information (until it
	 * sees "done"). 
	 * 
	 * @param filename the user information file name
	 * @throws FileNotFoundException if the user information file is not found, 
	 * then the main method catches the exception and asks user for another file name.
	 */
	
	private static void loadUsers(String filename) throws FileNotFoundException {
		// try to open file with given file name
		try{
			Scanner scan = new Scanner(new File(filename));
			// as long as file still has next line
			while (scan.hasNextLine()) {
				//TODO: account creation
				String firstName = scan.nextLine();
				String lastName = scan.nextLine();
				String username = scan.nextLine();
				String password = scan.nextLine();
				usernames.add(username);
				User u = userDB.createAccount(firstName, lastName, username, password);
				String title = scan.nextLine();
				// if creating account is successful, program will load the seen movies from the user file
				if (u != null) {
					while (!title.equals("done")) {
						if (!movieNames.contains(title)){
							movieNames.add(title);
						}
						Movie m = movieDB.getMovieByTitle(title);
						if (m == null) {
							m = new Movie(title, 0, null);
							movieDB.addMovie(m);
						}
						String tmp = scan.nextLine();
						u.addRating(m, Float.parseFloat(tmp));
						title = scan.nextLine();
					}
				} else {
					while (!title.equals("done")) {
						title = scan.nextLine();
					}
				}
			}
		} catch (Exception e){
			// throws exception if file was not found
			throw new FileNotFoundException();
		}
	}

	/**
	 * userMenu handles menu options that are available after logging in,
	 * such as search by title (and director if you have it) and the
	 * list of movies the user has seen before. 
	 * 
	 * @param u 
	 */
	private static void userMenu(User u) {
		Scanner scan = new Scanner(System.in);
		int choice3 = -1;
		while (choice3 != 0) {
			System.out.println("Welcome, " + u.getFirstName() + "! Select an option from the menu.");
			System.out.println("1 to search movies by title");
			System.out.println("2 to see the list of movies you have seen before and their ratings");
			System.out.println("3 to see the recommended movies");
			System.out.println("0 to logout and go back to the main menu");
			System.out.print("Enter your choice: ");
			choice3 = Integer.parseInt(scan.nextLine());
			switch (choice3) {
			// log out	
			case 0:
				System.out.println("Bye " + u.getFirstName() + " !"); 
				return;
			// search movies by title
			case 1:
				System.out.print("Enter keywords: ");
				String[] keywordsT = scan.nextLine().split(" ");

				ArrayList<Movie> searchResultsT = movieDB.searchByTitle(keywordsT);

				listMenu(u, searchResultsT);
				break;
				
			// get seen movies	
			case 2:	
				listMenu(u, u.getSeenMovies());
				break;
				
			// recommend movies	
			case 3:
				// created 2d array list of ratings
				ratingsDB = createRatingsDB();
				
				// get user index from username
				int userIndex = -1; 
				for (int i=0; i<usernames.size(); i++){
					if (usernames.get(i).equals(u.getUsername())){
						userIndex = i;
					}
				}
				
				// find similar users
				ArrayList<String> similarUsers = findSimilarUsers(userIndex);
				// main user's seen movies
				ArrayList<Movie> mainUser = u.getSeenMovies();
				// ArrayList of movies that are to be recommended to main user
				ArrayList<Movie> movies = new ArrayList<Movie>();
				// to make sure no movies are replicate
				for (int i= 0; i<similarUsers.size(); i++){
					ArrayList<Movie> newSet = recommendMovie(mainUser, similarUsers.get(i));
					for (int j=0; j<newSet.size(); j++){
						if (!movies.contains(newSet.get(j))){
							movies.add(newSet.get(j));
						}
					}
				}
				// Prints out recommended movies and it's expected rating
				for (int a=0; a<movies.size(); a++){
					for (int b=0; b<similarUsers.size(); b++){
						ArrayList<MovieRating> recMovie = userDB.seenMoviesOtherUsers(similarUsers.get(b));
						for (int c=0; c<recMovie.size(); c++){
							if ((recMovie.get(c).getMovie()).equals(movies.get(a))){
								System.out.println((a +1) + " " + (recMovie.get(c).getMovie().title) + ": " + recMovie.get(c).getRating() + " (expected)");
							}
						}
						
					}
				}
				
				// asks if user wants to rate or watch recommended movies
				int choice2 = -1;
				while (choice2 != 0) {
					System.out.println("Select the movie number to rate or watch");
					System.out.println("0 to go back to previous menu");
					System.out.print("Enter your choice: ");
					choice2 = Integer.parseInt(scan.nextLine());
					if (choice2 == 0) {
						
					} else if (choice2 - 1 >= 0 && choice2 <= movies.size()) {
						boolean doneRating = false;
						// while rating has not been correctly perform, 
						    //it was ask user to rate it correctly again and again
						while (doneRating == false){
							Movie m = movies.get(choice2 - 1);
							System.out.println("How did you like " + m.title
									+ "? (0.5~5 stars)");
							Float userinput = Float.parseFloat(scan.nextLine());
							if (userinput >= minRating && userinput <= maxRating){
								   // adds rating to user
								   u.addRating(m, userinput);
								   // adds rating to movie
								   m.addRating(userinput);
								   doneRating = true;
							} else {
								// if rating is out of requirement, prints invalid rating message  
								System.out.println("Invalid Rating. Please rate again. (0.5~5 stars please)");
							}
						}
						

					}
				}
				
			}
		}
	}
	
	// methods that takes in arraylist of main user's seen movies and username of similar user
	// this returns a set of movies that wants to be recommended to main user 
	private static ArrayList<Movie> recommendMovie(ArrayList<Movie> mainUser, String u){
		ArrayList<Movie> movieSet = new ArrayList<Movie>();
		ArrayList<Movie> otherUser = userDB.getSeenMovies1(u);
		for (int i=0; i<otherUser.size(); i++){
			if (!mainUser.contains(otherUser.get(i))){
				movieSet.add(otherUser.get(i));
			}
		}
		return movieSet;
	}
	
	// finds similar users of main user
	private static ArrayList<String> findSimilarUsers(int i){
		ArrayList<String> similar = new ArrayList<String> ();
		double[] list = new double[ratingsDB.length];
        for (int j = 0; j < ratingsDB.length; j++){
            // makes sure user 1 is being compared to a user not itself
            if (j!=i){
                
                double common = 0;
                int total = 0;
                // for each movie
                for (int z = 0; z < ratingsDB[0].length; z++){
                    // if they both watched the movie z
                    if (ratingsDB[i][z] != -1 && ratingsDB[j][z] != -1){
                        common = common + (Math.abs(ratingsDB[i][z] - ratingsDB[j][z]));
                        total += 1;
                    }
                }
                // if total does not equal zero
                if (total!= 0){
                    list[j] = common/total;
                } else {
                    // I put it as high number because there would be no rating difference 
                    //    higher than this since each rating can only be between 0.5 to 5
                    list[j] = Double.MAX_VALUE;
                }
                
            } else {
                // I put it as high number because there would be no rating difference 
                //   higher than this since each rating can only be between 0.5 to 5
                list[j] = Double.MAX_VALUE;
            }
        }
        double min = Double.MAX_VALUE;
        
        
        for (int y=0; y<list.length; y++){
            if (list[y] < min){
                min = list[y];
            }
        
        }

        for (int n = 0; n < list.length; n++){
            if (list[n] == min ){
                similar.add(usernames.get(n)); 
            }
        }
        return similar;
		
	}
	
	// create rating 2Darray
	private static double[][] createRatingsDB(){
		// create new ratings DB
		double[][] ratings = new double[usernames.size()][movieNames.size()];
		// store ratings into ratingsDB
		 // for each movie
		for (int i=0; i<movieNames.size(); i++){
			// for each user
			for (int j=0; j< usernames.size(); j++){
				// if user watched movie, store rating
				ArrayList <MovieRating> seen = userDB.seenMoviesOtherUsers(usernames.get(j));
				// for each seen movie
				boolean s = false;
				for (int k=0; k<seen.size(); k++){
					if (movieNames.get(i).equals(seen.get(k).getMovie().title)){
						ratings[j][i] = seen.get(k).getRating();
						s = true;
					}
					
				}
				if (s == false){
					ratings[j][i] = -1;
				}
				
			}
		}
		return ratings;
	}

	/**
	 * listMenu handles printing the list of Movies with the appropriate
	 * rating (user's own if available, average otherwise), letting user
	 * rate any of the Movies in the list.  
	 * 
	 * @param u
	 * @param list search result or the list of movies user has seen
	 */
	
	//every time add rating , add to both user and movie.
	
	private static void listMenu(User u, ArrayList<Movie> list) {
		Scanner scan = new Scanner(System.in);
		if (list.size() == 0){
			System.out.println("No Results Found");
		} else{
			for (int i = 0; i < list.size(); i++) {
				Movie m = list.get(i);
				System.out.println((i + 1) + ". " + m + "\n" + u.getRating(m));
			}
		}
		
		int choice = -1;
		while (choice != 0) {
			System.out.println("Select the movie number to rate or watch");
			System.out.println("0 to go back to previous menu");
			System.out.print("Enter your choice: ");
			choice = Integer.parseInt(scan.nextLine());
			if (choice == 0) {
				return;
			} else if (choice - 1 >= 0 && choice <= list.size()) {
				boolean doneRating = false;
				// while rating has not been correctly perform, 
				    //it was ask user to rate it correctly again and again
				while (doneRating == false){
					Movie m = list.get(choice - 1);
					System.out.println("How did you like " + m.title
							+ "? (0.5~5 stars)");
					Float userinput = Float.parseFloat(scan.nextLine());
					if (userinput >= minRating && userinput <= maxRating){
						   // adds rating to user
						   u.addRating(m, userinput);
						   // adds rating to movie
						   m.addRating(userinput);
						   doneRating = true;
					} else {
						// if rating is out of requirement, prints invalid rating message  
						System.out.println("Invalid Rating. Please rate again. (0.5~5 stars please)");
					}
				}
				

			}
		}
	}
}