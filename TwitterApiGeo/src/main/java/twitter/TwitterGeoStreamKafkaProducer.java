package twitter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.Location.Coordinate;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import kafka.serializer.StringEncoder;
import kafka.serializer.DefaultEncoder;

import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

public class TwitterGeoStreamKafkaProducer implements Runnable
{
	private String consumerKey; 
	private String consumerSecret;
	private String token;
	private String secret;
	private Client twitterClient;
	private Properties kafkaProperties;
	private ArrayList<Location> locations;
	private PrintWriter writer;
	private Producer<String, byte[]> producer;
	

	public TwitterGeoStreamKafkaProducer (String consumerKey, String consumerSecret, String token, String secret, ArrayList<Location> locations, Properties kafkaProperties) throws FileNotFoundException, UnsupportedEncodingException
	{
		this.consumerKey = consumerKey; 
		this.consumerSecret = consumerSecret;
		this.token = token;
		this.secret = secret;
		this.locations = locations;
		this.kafkaProperties = kafkaProperties;
		this.twitterClient = null;

	}
	
	public void run() 
	{
	    BlockingQueue<String> twitterStreamQueue = new LinkedBlockingQueue<String>(10000);
	    StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();


	    endpoint.locations(this.locations);
	    Authentication auth = new OAuth1(this.consumerKey, this.consumerSecret, this.token, this.secret);
        
	    this.twitterClient = new ClientBuilder()
	            .hosts(Constants.STREAM_HOST)
	            .endpoint(endpoint)
	            .authentication(auth)
	            .processor(new StringDelimitedProcessor(twitterStreamQueue))
	            .build();

	    // Establish a connection with twitter api
	    this.twitterClient.connect();

	    kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); 
	    kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer"); 

	    try
	    {
		    this.producer = new KafkaProducer<String,byte[]>(this.kafkaProperties);
		    
		    int count = 0;
		    while (true)
		    {
		      try {
		    	  String msg = twitterStreamQueue.take();
		    	  //if tweet has coordinates informed then it will send to kafka topic
		    	  if (msg.contains("\"coordinates\":{\"type\":\"Point\""))
		    	  {
		    		  String messageId = UUID.randomUUID().toString();
		    	  
		    		  byte[] tweetAvro = convertJsonToAvro(msg);
		    		  if (tweetAvro != null)
		    		  {
		    			ProducerRecord<String, byte[]> tweetRecord = new ProducerRecord<String, byte[]>("tweetsMadrid", messageId, tweetAvro);
		    		  	producer.send(tweetRecord);	
		    		  	count ++;
		    		  	System.out.println(" Geolocalizados: " + count);
		    		  }
		    	  }
		    	  
		      }
		      catch (InterruptedException ex)
		      {
		    	  System.out.println("Catch InterruptedException Twitter Stream: " + ex.getMessage());
		      }
		    }
	    }catch (Exception e)
	    {
	    	System.out.println("Kafka Producer error: " + e.getMessage());
	    	e.printStackTrace();
	    	return;
	    }

	    
	 
	}
	
	private byte[] convertJsonToAvro (String tweetJson)
	{
		InputStream input = new ByteArrayInputStream(tweetJson.getBytes());
		DataInputStream din = new DataInputStream(input);
		try {
			JsonAvroConverter converter = new JsonAvroConverter();
			GenericData.Record datum = converter.convertToGenericDataRecord(tweetJson.getBytes(),twitter.Tweet.SCHEMA$);
			String coordinates = datum.get("coordinates").toString();
			//this.writer.write(latitude + "," + longitude + "\n");
			//System.out.println("Longitude: " + longitude + " Latitude: " + latitude);
	        System.out.println("Tweet coordinates: " + coordinates);
			GenericDatumWriter<Object>  w = new GenericDatumWriter<Object>(Tweet.SCHEMA$);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Encoder e = EncoderFactory.get().binaryEncoder(outputStream, null);
			w.write(datum, e);
			e.flush();

			return outputStream.toByteArray();
		}catch (Exception ex)
		{
			System.out.println("Json To Avro Exception. Message: " + ex.getMessage());
			return null;
		}
	}
	
	
	public void stop () {
		
		if (this.twitterClient != null)
		{
			this.twitterClient.stop();
			this.producer.close();
		}
	}
	
}