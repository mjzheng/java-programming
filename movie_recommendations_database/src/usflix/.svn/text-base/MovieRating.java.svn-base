package usflix;

public class MovieRating {
	
	// Self Note: there is only 1 rating per movie because the rating is only from user
	
	// creates instance variables
	private Movie movie;
	private float rating;
	
	// creates rating "rules"
	protected static final Float maxRating = (float) 5; 
	protected static final Float minRating = (float) 0.5;
	
	// creates constructor 
	public MovieRating(Movie m, float r){
		movie = m;
		rating = r;
	}
	
	// creates toString method
	public String toString(){
		// if user doesn't have rating for the movie, it will return the average rating
		if (rating > 0){
			return Float.toString(rating);
		}else{
			return Float.toString(movie.getAverageRating())+" (average)";
		}
	}
	
	public Movie getMovie(){
		return movie;
	}
	
	public float getRating(){
		return rating;
	}
	
	// it will set rating if only it is between max and min rating
	public void setRating(float r){
		if (r >= minRating || r <= maxRating){
			rating = r ;
		}
	}

}
