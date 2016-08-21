package entities;


import java.util.ArrayList;

public class GoogleGeocodingResponse {
	
	private ArrayList<VenueLocation> results;

	public ArrayList<VenueLocation> getResults() {
		return results;
	}

	public void setResults(ArrayList<VenueLocation> results) {
		this.results = results;
	} 
	
}