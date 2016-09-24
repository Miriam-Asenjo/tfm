package tfm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

@Path("/neighbourhoodservice")
public class NeighbourHoodService {
	
	@Path("moreactivity")
	@GET
	@Produces("application/json")
	public Response getNeighbourhoodsMostActivity() 
	{
		try 
		{
			MongoClient mongoClient = new MongoClient("52.11.147.217");
			MongoDatabase db = mongoClient.getDatabase("taxiwisedb");		
	        DateTime lowerBound = DateTime.now().withZone(DateTimeZone.UTC).minusMinutes(40);
	        
			BasicDBObject lowerBoundCriteria = new BasicDBObject();
			lowerBoundCriteria.put("updatedAt", new BasicDBObject("$gt",new java.util.Date(lowerBound.getMillis())));

	        FindIterable<Document> iterableRankingBoroughs = db.getCollection("rankingboroughs").find(lowerBoundCriteria).sort(new Document("rank", -1));
	        StringBuilder str = new StringBuilder();
	        int index = 0;
	        for (Document rankingBoroughs : iterableRankingBoroughs) {
	            
                if (index == 0)
                	str.append(rankingBoroughs.toJson());
                else 
                	str.append(", " + rankingBoroughs.toJson());
                index ++;
	        }
	        
	        mongoClient.close();
	        String jsonEvents = String.format("[%s]", str.toString());
			return Response.status(200).entity(jsonEvents).build();	  
	        
		}catch (Exception e)
		{
			return Response.status(500).build();
		}
		
	}
	

}
