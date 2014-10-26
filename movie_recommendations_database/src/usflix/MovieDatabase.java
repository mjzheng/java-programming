package usflix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MovieDatabase {
	
	// creates instance variable
	private ArrayList<Movie> movies;
	
	// creates constructor (non default)
	public MovieDatabase(){
		movies = new ArrayList<Movie>(); 
	}
	
	// creates default constructor
	public MovieDatabase(String filename){
		// create new movies object
        movies = new ArrayList<Movie> ();
        // reads file
		try{
			Scanner filescan = new Scanner ( new File (filename));
			// while file still has next line, it will add to the movies arrayList
			while (filescan.hasNextLine()){
				addMovie((new Movie(filescan.nextLine(), Integer.parseInt(filescan.nextLine()), filescan.nextLine())));
			}
	
			
		} catch (FileNotFoundException e){
			System.out.println("Movie Data file is not found.");
		}
	}
	
	public boolean addMovie(Movie m){
		movies.add(m);
		return movies.contains(m);
	}
	
	public ArrayList<Movie> searchByTitle(String[] keywords){
	//Search for the movies that contain all the keywords given in the parameter. 
	// Returns ArrayList of all matching Movie objects.
	ArrayList<Movie> matchingTitles = new ArrayList<Movie> (); 
		// for each movie
		for (int i = 0; i < movies.size(); i ++ ){
			String[] titleArray = movies.get(i).title.toLowerCase().replaceAll("[^A-Za-z0-9]", " ").split(" ");
			// counts how much keywords matches a movie name
			int count = 0;
			// for each keywords
			for (int z = 0; z < keywords.length; z ++ ){
			    // for each title's words
				for (int j = 0; j < titleArray.length; j ++ ){
					// if a keyword matches word in titleArray, count = count + 1
					if (titleArray[j].equals(keywords[z])){
						count += 1;
					}

				}
				
			} 
			// if title contains all keywords, movie object gets added to the arraylist, matchingTitles
			if (count == keywords.length){
				matchingTitles.add(movies.get(i));
			}
			
		}
		return matchingTitles;
	
	}

	
	public Movie getMovieByTitle(String title){
	   // Movie returns the first Movie object that matches the given title. if none matches, then return null.
	   for ( int i = 0; i < movies.size(); i++){
	      if (movies.get(i).title.equals(title)){
	          return movies.get(i);
	       }
	   }
	   return null;       
	}

}
