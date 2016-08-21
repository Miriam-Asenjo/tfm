package db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DbClient  
{
	private MongoDatabase mongoDatabase;
	
	public DbClient (String hostName, String databaseName)
	{
		this.mongoDatabase = new MongoClient(hostName, 27017).getDatabase(databaseName);
	}
	
	public MongoDatabase getDatabase() 
	{
		return this.mongoDatabase;
	}

}
