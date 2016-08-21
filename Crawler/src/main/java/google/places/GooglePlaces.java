package google.places;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GooglePlaces {
	
	
	public static void GetPlacesByType() {
	
		ArrayList<Place> stations = new ArrayList<Place>();
		try {

			boolean pendingPlaces = true;
			String nextPageToken = null;
			URL url = null;
			while (pendingPlaces == true)
			{
				
				if (nextPageToken == null)
					url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyAAYV1LNWP-1WasMf4pTBA5cmR9Nrs_0Uw&location=40.4168,-3.7024&radius=20000&types=subway_station&language=es");
				else 
					url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=" + nextPageToken + "&key=AIzaSyAAYV1LNWP-1WasMf4pTBA5cmR9Nrs_0Uw");
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				String strTemp = "";
				StringBuilder jsonResponse = new StringBuilder();
				while (null != (strTemp = br.readLine())) {
					System.out.println(strTemp);
					jsonResponse.append(strTemp);
				}
				System.out.println("json Response: " + jsonResponse);
				Gson gson = new GsonBuilder().create();
				GoogleApiResponse googleApiResponse=gson.fromJson(jsonResponse.toString(), GoogleApiResponse.class);
				System.out.println("Getting Google Api Response");
				stations.addAll(googleApiResponse.getResults());
				nextPageToken = googleApiResponse.getNext_page_token();
				pendingPlaces = googleApiResponse.getNext_page_token() != null;
				Thread.sleep(20000);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Num Places: " + stations.size());
	}
	
	public static void main(String[] args) {
		GooglePlaces.GetPlacesByType();

	}

}
