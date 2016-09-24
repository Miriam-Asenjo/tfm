package tfm;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.joda.time.DateTime;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

@Path("/events")
public class EventService {

	@Path("today")
	@GET
	@Produces("application/json")
	public Response getEventsToday() {
		
		try 
		{
			MongoClient mongoClient = new MongoClient("52.11.147.217");
			MongoDatabase db = mongoClient.getDatabase("taxiwisedb");		
	        DateTime today = DateTime.now();
	        DateTime endToday = new DateTime(today.getYear(),today.getMonthOfYear(),today.getDayOfMonth(),23,59);
	
	        FindIterable<Document> iterableEvents = getEventsInDateRange(today, endToday, db);
	        StringBuilder str = new StringBuilder();
	        int index = 0;
	        for (Document event : iterableEvents) {
	            
                if (index == 0)
                	str.append(event.toJson());
                else 
                	str.append(", " + event.toJson());
                index ++;
	        }
	        
	        mongoClient.close();
	        String jsonEvents = String.format("[%s]", str.toString());
			return Response.status(200).entity(jsonEvents).build();	        
		}
		catch (Exception e)
		{
			return Response.status(500).build();	
		}
	}
	
	@Path("tomorrow")
	@GET
	@Produces("application/json")
	public Response getEventsTomorrow() {
		
		try 
		{
			MongoClient mongoClient = new MongoClient("52.11.147.217");
			MongoDatabase db = mongoClient.getDatabase("taxiwisedb");		
	        DateTime tomorrow = DateTime.now().plusDays(1);
	        DateTime tomorrowLowerBound = new DateTime(tomorrow.getYear(),tomorrow.getMonthOfYear(),tomorrow.getDayOfMonth(),0,0);
	        DateTime tomorrowUpperBound = new DateTime(tomorrow.getYear(),tomorrow.getMonthOfYear(),tomorrow.getDayOfMonth(),23,59);
	
	        FindIterable<Document> iterableEvents = getEventsInDateRange(tomorrowLowerBound, tomorrowUpperBound, db);
	        StringBuilder str = new StringBuilder();
	        int index = 0;
	        for (Document event : iterableEvents) {
	            
                if (index == 0)
                	str.append(event.toJson());
                else 
                	str.append(", " + event.toJson());
                index ++;
	        }
	        
	        mongoClient.close();
	        String jsonEvents = String.format("[%s]", str.toString());
			return Response.status(200).entity(jsonEvents).build();	        
		}
		catch (Exception e)
		{
			return Response.status(500).build();	
		}
	}
	
	private FindIterable<Document> getEventsInDateRange(DateTime lowerBound, DateTime upperBound, MongoDatabase db)
	{
        
        BasicDBObject criteria = new BasicDBObject();
		List<BasicDBObject> criteriaParts = new ArrayList<BasicDBObject>();
		BasicDBObject lowerBoundCriteria = new BasicDBObject();
		lowerBoundCriteria.put("time", new BasicDBObject("$gte",new java.util.Date(lowerBound.getMillis())));
		criteriaParts.add(lowerBoundCriteria);
		BasicDBObject upperBoundCriteria = new BasicDBObject();
		upperBoundCriteria.put("time", new BasicDBObject("$lt",new java.util.Date(upperBound.getMillis())));
		criteriaParts.add(upperBoundCriteria);
		BasicDBObject eventsCriteria = new BasicDBObject();
		eventsCriteria.put("$and", criteriaParts);

        FindIterable<Document> iterableEvents = db.getCollection("events").find(eventsCriteria);
        return iterableEvents;
		
	}
	
}
