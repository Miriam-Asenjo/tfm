package google.places;

import java.util.ArrayList;

public class GoogleApiResponse {

	private String next_page_token;
	private ArrayList<Place> results;

	public String getNext_page_token() {
		return next_page_token;
	}

	public ArrayList<Place> getResults() {
		return results;
	}

	public void setResults(ArrayList<Place> results) {
		this.results = results;
	}

	public void setNext_page_token(String next_page_token) {
		this.next_page_token = next_page_token;
	}

}
