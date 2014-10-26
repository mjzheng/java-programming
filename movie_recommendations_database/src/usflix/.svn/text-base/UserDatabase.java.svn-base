package usflix;

import java.util.ArrayList;
import java.util.HashMap;

public class UserDatabase{
	
	// creates instance variables
	public static final int minPwdLength = 6; 
	private HashMap<java.lang.String,User> userDB;
	
	// creates constructor
	public UserDatabase(){
		userDB = new HashMap<String,User>();
	}
	
	// creates create Account method
	public User createAccount( String f, String l, String u, String p){
		// if username is unique or password is more than min password length or password contains username, creating account fails and returns null
		if ( (isAvailable(u) == false) || (p.contains(u)) || (p.length() < minPwdLength) ){
			return null;
			// return null; 
		} else {
			// returns user object or new user successfully gets created
			User newUser = new User(f, l, u, p);
			userDB.put( u, newUser );
			return userDB.get(u); 
		}
	}
	
	// logs in user
	public User login( String u, String p){
		User loginUser = userDB.get(u);
		// if log in successful, returns loginUser
		if (loginUser.login(p)){
			return loginUser;
		} else {
			// if not, return null
			return null;
		}
	}
	
	public ArrayList<MovieRating> seenMoviesOtherUsers (String u){
		return userDB.get(u).getSeenMovies2();
	}
	
	public ArrayList<Movie> getSeenMovies1(String u){
		return userDB.get(u).getSeenMovies();
	}
	
	public double getOtherMovieRating (String u, Movie m){
		return Double.parseDouble(userDB.get(u).getRating(m)); 
	}
	
	// checks to see if a username is available or not
	public boolean isAvailable(String u){
		if (userDB.get(u) == null){
			return true;
		} else {
			return false; 
		}
	}
	
	

}
