package tfm;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Crunchify.com
 */
 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Path("/taxistandservice")
public class TaxiStandService {
	
	@Path("stands")
	@GET
	@Produces("application/json")
	public Response getTaxiStands () {
 
		try 
		{
			MongoClient mongoClient = new MongoClient("52.11.147.217");
			MongoDatabase db = mongoClient.getDatabase("taxiwisedb");
	    	MongoCollection<Document> collection = db.getCollection("taxistands");
	    	FindIterable<Document> taxiIterable = collection.find();
	    	StringBuilder str = new StringBuilder();
	    	int index = 0;
	    	for (Document taxiStand: taxiIterable)
	    	{
                if (index == 0)
                	str.append(taxiStand.toJson());
                else 
                	str.append(", " + taxiStand.toJson());
                index ++;
	    	}
	    	
	    	String jsonTaxiStands = String.format("[%s]", str.toString());
	    	mongoClient.close();
			return Response.status(200).entity(jsonTaxiStands).build();
		}
		catch (Exception e)
		{
			return Response.status(500).build();
		}
    	
	}
	
	 @Path("{la}/{lon}")
	 @GET
	 @Produces("application/json")
	 public Response getBusiestStands(@PathParam("la") float latitude, @PathParam("lon") float longitude) throws JSONException {

		 try
		 {
			MongoClient mongoClient = new MongoClient("52.11.147.217");
			MongoDatabase db = mongoClient.getDatabase("taxiwisedb");
			AggregateIterable<Document> nearestStandsMostActivity = getNeareastStandsMostActivity(latitude, longitude,db);
			
			StringBuilder str = new StringBuilder();
			String lastStand = null;
	        if (nearestStandsMostActivity != null)
	        {
	        	int index = 0;
	        	for (Document docTaxiStand : nearestStandsMostActivity)
	            {
	                if (index == 0)
	                	str.append(docTaxiStand.toJson());
	                else if (index <= 2)
	                	str.append(", " + docTaxiStand.toJson());
	                
	                if (index > 2)
	                	lastStand = docTaxiStand.toJson();
	                index ++;
	            }
	        }	
	        
		 
	        mongoClient.close();
	        String jsonTaxiStands = null;
	        if (lastStand != null)
	        	jsonTaxiStands = String.format("[%s,%s]", str.toString(), lastStand);
	        else 
	        	jsonTaxiStands = String.format("[%s]", str.toString());
			return Response.status(200).entity(jsonTaxiStands).build();
		 }
		 catch (Exception e)
		 {
			return Response.status(500).build();			 
		 }
	}
	 
	 
	private AggregateIterable<Document> getNeareastStandsMostActivity(float latitude, float longitude, MongoDatabase db) 
	{

        double[] currentLoc = new double[] {longitude,latitude};
		
		List<BasicDBObject> andParts = new ArrayList<BasicDBObject>();
		BasicDBObject count = new BasicDBObject();
		//take those taxi stands with more outs than ins
		count.put("count", new BasicDBObject("$lt",0));
		andParts.add(count);
		BasicDBObject groupedTime = new BasicDBObject();
		//consider events that occurs 30 minutes ago till now
		DateTime dateTime = DateTime.now().withZone(DateTimeZone.UTC).minusMinutes(10);
		groupedTime.put("groupedTimestamp", new BasicDBObject("$gte",new java.util.Date(dateTime.getMillis())));
		andParts.add(groupedTime);
		
		BasicDBObject andQuery = new BasicDBObject();
		andQuery.put("$and", andParts);
		
		
		BasicDBObject point = new BasicDBObject();
        point.put("type", "Point");
        point.put("coordinates", currentLoc);
		
		DBObject geoNearFields = new BasicDBObject();
        geoNearFields.put("near", point);
        geoNearFields.put("distanceField", "dis");
        geoNearFields.put("distanceMultiplier", 1);
        geoNearFields.put("maxDistance", 2000); //2km in radiants
        geoNearFields.put("spherical",true);
        geoNearFields.put("query", andQuery);
        DBObject geoNear = new BasicDBObject("$geoNear", geoNearFields);

        
        DBObject sortFields = new BasicDBObject("totalOut", 1);
        DBObject sort = new BasicDBObject("$sort", sortFields);
        
        Map<String, Object> groupByFields = new HashMap<String, Object>();
        groupByFields.put("taxiStandId", "$taxiStandId");
        groupByFields.put("coordinates", "$loc.coordinates");
        DBObject groupFields = new BasicDBObject( "_id", new BasicDBObject(groupByFields));
        groupFields.put("totalOut", new BasicDBObject( "$sum", "$count"));
        DBObject group = new BasicDBObject("$group", groupFields);
        
        //return only stands where overall aggregation during last 3 minutes has at least 5 exits
		BasicDBObject totalOut = new BasicDBObject();
		totalOut.put("totalOut", new BasicDBObject("$lt",-2));
		DBObject match = new BasicDBObject("$match", totalOut);
        
        AggregateIterable<Document> output = null;
        try {
        	MongoCollection collection = db.getCollection("taxistandseventsaggregate");
        	output = collection.aggregate(Arrays.asList(geoNear, group, match, sort));
        }
        catch (MongoException e) {
        	e.printStackTrace();
        }
        
        return output;

	}


}