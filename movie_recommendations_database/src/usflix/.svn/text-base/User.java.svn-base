package usflix;

import java.util.ArrayList;

public class User {
	
	// creates instance variables
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private ArrayList<MovieRating> seenMovies;
	
	// creates class variables for ratings min and max
	protected static final Float maxRating = (float) 5; 
	protected static final Float minRating = (float) 0.5;

	// creates constructor 
	public User( String f, String l, String u, String p){
		firstName = f;
		lastName = l;
		username = u;
		password= p;
		seenMovies = new ArrayList<MovieRating>();
	}
	
	// returns if login was succesful or not
	public boolean login(java.lang.String p){
		if (p.equals(password)){
			return true;
		}else{
			return false;
		} 
	}
	
	public void addRating(Movie m,float r){
		
		// adds rating if only r is between max and min rating
		if (r >= minRating && r <= maxRating){
		
			// indicates if it's in seenMovie or not
			boolean isInSeenMovies = false; 
			// TODO: checks to see if it's in seenMovies
			for (int i=0; i< seenMovies.size(); i++){
				// if movie is in seenMovies 
				if (seenMovies.get(i).getMovie().equals(m)){
					isInSeenMovies = true;
					// get current rating for movie m 
					Float current = seenMovies.get(i).getRating(); 
					// TODO: update rating in MovieRating object
					seenMovies.get(i).setRating(r);
					// TODO: update rating in the Movie object
					// use the current rating on removeRating method
					m.removeRating(current); 
					// add new rating
					m.addRating(r);
				}
			}   
			// TODO: if not in seenMovies, add rating in Movie Object
			   // create new MovieRating Object and add it to seenMovies
			if (isInSeenMovies == false){
				m.addRating(r);
				seenMovies.add(new MovieRating(m, r)); 
			}
		}

	}
	
	public String getRating(Movie m){
		// if movie m is in seen movies, return the movierating object in seenmovies
		for (int i=0; i< seenMovies.size(); i++){
			if (seenMovies.get(i).getMovie().equals(m)){
				return Float.toString(seenMovies.get(i).getRating()); 
			}
		}
		// return average of not
		return Float.toString(m.getAverageRating())+"(average)";
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public String getUsername(){
		return username;
	}
	
	public ArrayList<Movie> getSeenMovies(){
		// returns an arraylist of movie objects from seen movies
		ArrayList<Movie> seenMovieObjects = new ArrayList<Movie> ();
		for (int i=0; i<seenMovies.size(); i++){
			seenMovieObjects.add(seenMovies.get(i).getMovie());
		} 
		return seenMovieObjects; 
	}	
	
	public ArrayList<MovieRating> getSeenMovies2 () {
		return seenMovies;
	}
}
