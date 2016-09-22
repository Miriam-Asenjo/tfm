package crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.joda.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

import db.DbClient;
import entities.Event;
import entities.EventType;
import entities.GeoMongo;
import entities.GoogleGeocodingResponse;
import entities.Venue;

public class GuiaDelOcioCrawler extends BaseCrawler {
	
	public static final String THEATERHOMEPAGE = "http://www.guiadelocio.com/madrid/teatro-y-danza/representaciones/(cartel)";
	public static final String CONCERTHOMEPAGE = "http://www.guiadelocio.com/madrid/conciertos/actuaciones";
	
	public static final String THEATERURL = "teatro-y-danza";
	public static final String CONCERTURL = "conciertos";
	
	private final String _fechas1 = "^Fechas: Desde (\\d{2}/\\d{2}/\\d{4})$";
	private final Pattern _patternFechas1 = Pattern.compile(_fechas1);
	
	private final String _fechas2 = "^Fechas: Desde (\\d{2}/\\d{2}/\\d{4}) hasta (\\d{2}/\\d{2}/\\d{4})$";
	private final Pattern _patternFechas2 	= Pattern.compile(_fechas2);
	
	private final String _fechas3 = "^Fechas: (\\d{2}/\\d{2}/\\d{4})$";
	private final Pattern _patternFechas3 	= Pattern.compile(_fechas3);

	private Map<String, Event> theaterPlays;
	private Map<String, Event> concerts;
	private Map<String,Venue> places;
	
	private DbClient dbClient;
	
	public GuiaDelOcioCrawler(DbClient dbClient){
		this.theaterPlays = new HashMap<String,Event>();
		this.concerts = new HashMap<String,Event>();
		this.dbClient = dbClient;
		this.places = this.getPlaces();
	}
	
	public void getTheaterEventInfo (String eventUrl, String startDate, String endDate) throws Exception
	{
		System.out.println("Get Theater Info: " + eventUrl);
		Response response = this.sendRequest(eventUrl);
		
		if (response.isSuccessful()){

	        HtmlCleaner cleaner = new HtmlCleaner();
	        CleanerProperties props = cleaner.getProperties();
	        props.setAllowHtmlInsideAttributes(true);
	        props.setAllowMultiWordAttributes(true);
	        props.setRecognizeUnicodeChars(true);
	        props.setOmitComments(true);
	        
	        TagNode node = cleaner.clean(response.getContent());
	        
	        Object[] eventElements = node.evaluateXPath("//div[@class='detail-page']/header/h1/a");
	        String eventName = null;
	        if (eventElements.length > 1)
	        	eventName = ((TagNode)(eventElements[1])).getText().toString();
	        else
	        	eventName = ((TagNode)(eventElements[0])).getText().toString();

	        TagNode firstSection = (TagNode)node.evaluateXPath("//div[@class='detail-page']/section")[0];
	        
	        Object[] addresses = firstSection.evaluateXPath("//ul/li/strong[text() = 'Dirección:']");
	        String address = null;
	        if (addresses != null && addresses.length >= 1)
	        {
	        	address = ((TagNode)addresses[0]).getParent().getText().toString().replace("Dirección: ", "");
	        
		        Object[] completeAddressNodes = node.evaluateXPath("//ul/li/strong[text() = 'Dirección']");
		        if (completeAddressNodes != null && completeAddressNodes.length >= 1)
		        {
			        TagNode completeAdressNode = (TagNode)(completeAddressNodes[0]);
			        String addressBottom = completeAdressNode.getParent().getText().toString().replace("Dirección: ", "");
			        if (addressBottom != null && addressBottom!= "")
			        	address = addressBottom;
		        }
	        }

	        Object[] places = firstSection.evaluateXPath("//ul/li/strong[text() = 'Dónde:']");
	        String place = null;
	        if (places != null && places.length >= 1)
	        {
	        	place  = ((TagNode)places[0]).getParent().getText().toString().replace("Dónde: ", "");
	        }
	        
	        Object[] timeObjects = firstSection.evaluateXPath("//ul/li/strong[text() = 'Horarios:']");
	        if (timeObjects.length > 0)
	        {
	        	String time  = ((TagNode)(timeObjects[0])).getParent().getText().toString().replace("Horarios: ", "");
		        ArrayList<LocalDateTime> eventTimes = EventParserUtils.ParseTheaterTimetable(time, startDate, endDate);
		        if (!theaterPlays.containsKey(eventName) && eventTimes.size() > 0 && address != null)
		        {
		        	Event event = new Event(eventName, address, place, eventTimes, EventType.THEATREDANCE);
		        	Venue venue = getVenueForEvent(event);
		        	if (venue != null)
		        	{
		        		event.setVenue(venue);
		        		this.insertEvent(event);
		        		theaterPlays.put(eventName, event);
		        	}
		        		
		        }else if (eventTimes.size() == 0)
		        {
		        	System.out.println("Not events found");
		        }
	        }
		}
		
	}
	
	public void getConcertEventInfo (String eventUrl, String startDate, String endDate) throws Exception
	{
		System.out.println("Get Concert Event Info: " + eventUrl);
		Response response = this.sendRequest(eventUrl);
		
		if (response.isSuccessful()){
	        HtmlCleaner cleaner = new HtmlCleaner();
	        CleanerProperties props = cleaner.getProperties();
	        props.setAllowHtmlInsideAttributes(true);
	        props.setAllowMultiWordAttributes(true);
	        props.setRecognizeUnicodeChars(true);
	        props.setOmitComments(true);
	        
	        TagNode node = cleaner.clean(response.getContent());
	        
	        Object[] eventElements = node.evaluateXPath("//div[@class='detail-page']/header/h1");
	        String eventName = null;
	        if (eventElements.length > 1)
	        	eventName = ((TagNode)(eventElements[1])).getText().toString();
	        else
	        	eventName = ((TagNode)(eventElements[0])).getText().toString();
	        
	        TagNode firstSection = (TagNode)node.evaluateXPath("//div[@class='detail-page']/section")[0];
	        
	        TagNode direccionNode = (TagNode)firstSection.evaluateXPath("//ul/li/strong[text() = 'Dirección:']")[0];
	        String direccion = direccionNode.getParent().getText().toString().replace("Dirección: ", "");
	        
	        Object[] completeAddressNodes = node.evaluateXPath("//ul/li/strong[text() = 'Dirección']");
	        if (completeAddressNodes.length >= 1)
	        {
		        TagNode completeAdressNode = (TagNode)(completeAddressNodes[0]);
		        String direccionBottom = completeAdressNode.getParent().getText().toString().replace("Dirección: ", "");
		        if (direccionBottom != null && direccionBottom!= "")
		        	direccion = direccionBottom;
	        }
	        
	        TagNode placeNode = (TagNode)firstSection.evaluateXPath("//ul/li/strong[text() = 'Dónde:']")[0];
	        String place  = placeNode.getParent().getText().toString().replace("Dónde: ", "");

	        Object[] timeObjects = firstSection.evaluateXPath("//ul/li/strong[text() = 'Hora:']");
	        if (timeObjects.length > 0)
	        {
	        	String time  = ((TagNode)(timeObjects[0])).getParent().getText().toString().replace("Hora: ", "");
		        ArrayList<LocalDateTime> eventTimes = EventParserUtils.ParseConcertTimetable(time, startDate, endDate);
		        if (!concerts.containsKey(eventName) && eventTimes.size() > 0 && direccion != null)
		        {
		        	Event event = new Event(eventName, direccion, place, eventTimes, EventType.CONCERT);
		        	Venue venue = getVenueForEvent(event);
		        	if (venue != null)
		        	{
		        		event.setVenue(venue);
		        		this.insertEvent(event);
			        	concerts.put(eventName, event);
		        	}

		        }else if (eventTimes.size() == 0)
		        {
		        	System.out.println("Not events found Concert Time: " + time + " StartDate: " + startDate +  " EndDate: " + endDate);
		        }
	        }
		}
		
	}

	public Map<String, Event> getTheaterPlays() {
		return this.theaterPlays;
	}

	public Map<String, Event> getConcerts() {
		return this.concerts;
	}

	public void doCrawl(String url) throws Exception {

		Response response = this.sendRequest(url);

		if (response.isSuccessful()){
			
			EventType eventType = EventType.THEATREDANCE;
			if (url.contains(GuiaDelOcioCrawler.CONCERTURL))
				eventType = EventType.CONCERT;

	        HtmlCleaner cleaner = new HtmlCleaner();
	        CleanerProperties props = cleaner.getProperties();
	        props.setAllowHtmlInsideAttributes(true);
	        props.setAllowMultiWordAttributes(true);
	        props.setRecognizeUnicodeChars(true);
	        props.setOmitComments(true);
	        
	        TagNode node = cleaner.clean(response.getContent());
	        Object[] eventItems = node.evaluateXPath("//div[@class='general-result-page']/article");

	        for (Object eventPage: eventItems)
	        {
	        	TagNode eventTitleNode = (TagNode)(((TagNode)eventPage).evaluateXPath("//div/header/div/h1/a")[0]);
	        	String eventPageUrl = ((TagNode)eventTitleNode).getAttributeByName("href");
	        	TagNode eventDates = (TagNode)(((TagNode)eventPage).evaluateXPath("//div/header/div/p")[0]);
	        	String fechasText = eventDates.getText().toString();
	        	String fechaInicial = null;
	        	String fechaFinal = null;
	        	Matcher matcherFechas1 = _patternFechas1.matcher(fechasText);
	        	Matcher matcherFechas2 = _patternFechas2.matcher(fechasText);
	        	Matcher matcherFechas3 = _patternFechas3.matcher(fechasText);
	        	if (matcherFechas1.matches()) {
	        	    fechaInicial = matcherFechas1.group(1);
	        	}else if (matcherFechas2.matches()) {
	        	    fechaInicial = matcherFechas2.group(1);
	        	    fechaFinal = matcherFechas2.group(2);
     		
	        	}else if (matcherFechas3.matches()) {
	        	    fechaInicial = matcherFechas3.group(1);
	        	}
	        	
	        	if (eventType == EventType.THEATREDANCE)
	        		getTheaterEventInfo(eventPageUrl, fechaInicial, fechaFinal);
	        	else 
	        		getConcertEventInfo(eventPageUrl, fechaInicial, fechaFinal);
	        }
	        
	        Object[] pages = node.evaluateXPath("//div[@class='pager-film mt20']/div[@class='pull-right']/ul/li/a");
	        String nextPageUrl = null;
	        boolean currentPage = false;
	        for (Object page: pages) {
	        	nextPageUrl = ((TagNode)page).getAttributeByName("href");
	        	
	        	if (nextPageUrl != null && !nextPageUrl.equals("#") && currentPage)
	        	{
	        		break;
	        	}
	        	else if (nextPageUrl != null && nextPageUrl.equals("#"))
	        	{
	        		currentPage = true;
	        	}
	        	
	        }
	        
	        if ((nextPageUrl != null) && (nextPageUrl.startsWith("http")))
	        {
	        	doCrawl(nextPageUrl);
	        }
	     
	        
		}
		
	}
	
	public Venue getVenueForEvent(Event event)
	{
		String place = event.getPlace();
		String direccion = event.getAddress();
		if (!direccion.startsWith("Plaza") && !direccion.startsWith("Avenida"))
			direccion = "Calle " + direccion;
		
		if (!this.places.containsKey(place.toUpperCase()))
		{
			Venue venue = new Venue(place, direccion);
			if (tryGetCoordinatesVenue(venue))
			{
				this.insertPlace(venue);
				places.put(place.toUpperCase(), venue);
				return venue;
			}
			return null;
		}else {
			return this.places.get(place.toUpperCase());
		}
	}
	
	public Map<String,Venue> getEventPlaces() 
	{
		for (String nombreEvento: this.concerts.keySet())
		{
			Event event =  this.concerts.get(nombreEvento);
			String place = event.getPlace();
			String direccion = event.getAddress();
			if (!direccion.startsWith("Plaza") && !direccion.startsWith("Avenida"))
				direccion = "Calle " + direccion;
			
			if (!this.places.containsKey(place.toUpperCase()))
			{
				Venue venue = new Venue(place, direccion);
				if (tryGetCoordinatesVenue(venue))
					places.put(place.toUpperCase(), venue);
			}			
		}
		
		for (String nombreEvento: this.theaterPlays.keySet())
		{
			Event event =  this.theaterPlays.get(nombreEvento);
			String place = event.getPlace();
			String direccion = event.getAddress();
			if (!direccion.startsWith("Plaza") && !direccion.startsWith("Avenida"))
				direccion = "Calle " + direccion;
			
			if (!this.places.containsKey(place.toUpperCase()))
			{
				Venue venue = new Venue(place, direccion);
				if (tryGetCoordinatesVenue(venue))
					places.put(place.toUpperCase(), venue);
			}			
		}
		
		return this.places;
	}
	
	private boolean tryGetCoordinatesVenue(Venue venue) {
		
		try {
			
			String key = "AIzaSyA2m6rnE7yFTMID-jTAMdTulXlzLwvslF8";
			String apiGoogle =  String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%1$s&key=%2$s",URLEncoder.encode(venue.getAddress().replace("s/n",""),"UTF-8"), key);

			URL url = new URL(apiGoogle);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String strTemp = "";
			StringBuilder jsonResponse = new StringBuilder();
			while (null != (strTemp = br.readLine()))
			{
				jsonResponse.append(strTemp);
			}
			
			Gson gson = new GsonBuilder().create();
			GoogleGeocodingResponse googleGeocodingResponse = gson.fromJson(jsonResponse.toString(), GoogleGeocodingResponse.class);
			if (googleGeocodingResponse.getResults()!= null)
			{
				venue.setLatitude(googleGeocodingResponse.getResults().get(0).getGeometry().getLocation().getLat());
				venue.setLongitude(googleGeocodingResponse.getResults().get(0).getGeometry().getLocation().getLng());
				return true;
			}
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
			
	}
	
	
	public int getNumObrasTeatro() {
		return this.theaterPlays.size();
	}

	public int getNumConcerts () {
		return this.concerts.size();
	}
	
	public void insertEvent (Event event)
	{
		List<WriteModel<Document>> updates = new ArrayList<WriteModel<Document>>();

		for (LocalDateTime timeEvent: event.getTimes())
		{
			Document document = new Document();
			String id = String.format("%s_%s", event.getName().replace(" ","").toLowerCase(), timeEvent.toString());
			//document.append("_id", id);
			document.append("name", event.getName());
			document.append("place", event.getVenue().getName());
			document.append("address", event.getAddress());
			document.append("time", timeEvent.toDate());
			document.append("type", event.getTypeDescription());
			document.append("loc", new Document("type", "Point").append("coordinates",
					Arrays.asList(event.getVenue().getLongitude(), event.getVenue().getLatitude())));
	        Bson upsert =  new Document("$set",document);
			UpdateOneModel<Document> updateDocument = new UpdateOneModel<Document>(
	                new Document("_id",id),
	                upsert,
	                new UpdateOptions().upsert(true)
	            );
			
			updates.add(updateDocument);
		}
		
		Document weightEventDoc = new Document();
		String id = String.format("_%s", event.getName().replace(" ","").toLowerCase());
		weightEventDoc.append("name", event.getName());
		weightEventDoc.append("weight", 0.5);
		UpdateOptions options = new UpdateOptions().upsert(true);
		Bson filter = Filters.eq("_id", id);
        Bson upsert =  new Document("$set",weightEventDoc);
		dbClient.getDatabase().getCollection("weightevents").updateOne(filter, upsert, options);
		//dbClient.getDatabase().getCollection("weightEvents").insertOne(weightEventDoc);


		
		
		dbClient.getDatabase().getCollection("events").bulkWrite(updates);
		//dbClient.getDatabase().getCollection("places").updateM
		//dbClient.getDatabase().getCollection("places").insertMany(documents);
		
	}
	
	public void insertPlace (Venue venue) 
	{
        //JsonObject jsonPoint = getPoint(venue.getLongitude(), venue.getLatitude());

        Document document = new Document();
        String id = String.format("%s", venue.getName().replace(" ","").toLowerCase());
        //document.append("_id", id);
		document.append("name",venue.getName());
		document.append("address", venue.getAddress());
		document.append("weight", 1);
		//document.append("loc", jsonPoint.getAsJsonObject());
		document.append("loc", new Document("type", "Point").append("coordinates",
				Arrays.asList(venue.getLongitude(), venue.getLatitude())));
        Bson upsert =  new Document("$set",document);
		MongoCollection<Document> collection = dbClient.getDatabase().getCollection("places");
		long count = collection.count();	
		UpdateOptions options = new UpdateOptions().upsert(true);
		Bson filter = Filters.eq("_id", id);
		dbClient.getDatabase().getCollection("places").updateOne(filter, upsert, options);

	}
	
	public Map<String,Venue> getPlaces()
	{
		Map<String,Venue> venues = new HashMap<String,Venue>();
		
		MongoCollection<Document> collection = dbClient.getDatabase().getCollection("places");
		MongoCursor<Document> cursor = collection.find().iterator();
		try {
		    while (cursor.hasNext()) {
		        Document document = cursor.next();
		        String name = (String)document.get("name");
		        String address = (String)document.get("address");
		        Document location = (Document)document.get("loc");
		        //GeoMongo geoCoordinates = new Gson().fromJson(location, GeoMongo.class);
		        List<Double> coordinates = location.get("coordinates", ArrayList.class);
		        Venue venue = new Venue (name, address);
		        venue.setLongitude(coordinates.get(0).floatValue());
		        venue.setLatitude(coordinates.get(1).floatValue());
		        venues.put(name, venue);
		    }
		} finally {
		    cursor.close();
		}
		
		return venues;

	}
	
}
