package usflix;

import java.util.ArrayList;

public class Movie {
	
	// creates instance variables
	protected final String title;
	protected final int year; 
	protected final String director;
	private ArrayList<Float> ratings;
	
	// creates rating "rules"
	protected static final Float maxRating = (float) 5; 
	protected static final Float minRating = (float) 0.5;
	
	// creates constructor
	public Movie(String movieTitle, int yearOfRelease, String directorName){
		title = movieTitle;
		year = yearOfRelease;
		director = directorName;
		ratings = new ArrayList<Float>();
	}
	
	// creates toString Method
	public String toString(){
		return title + " " + "(" + year + ")" + "\nDirector: " + director;
	}
	
	// this returns the average rating of the movie
	public float getAverageRating(){
		Float sum = (float) 0;
		for (int i=0; i<ratings.size(); i++){
			sum += ratings.get(i);
		}
		return sum/ratings.size(); 
	}
	
	// add rating if it's between the max and min requirements
	public void addRating(float r){ 
		if (r >= minRating && r <= maxRating){
			ratings.add(r);
		}
	}
	
	// removes rating from movie ratings
	public void removeRating(float r){ 
		for (int i=0; i<ratings.size(); i++){
			if (ratings.get(i) == r){
				ratings.remove(i);
			}
		}
	}

}
