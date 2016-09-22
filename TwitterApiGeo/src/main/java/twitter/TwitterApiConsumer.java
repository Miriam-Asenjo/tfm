/**
 * Copyright 2013 Twitter, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * This is tfm 
 **/

package twitter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import com.twitter.hbc.core.endpoint.Location.Coordinate;
import com.twitter.hbc.core.endpoint.Location;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TwitterApiConsumer {

  public static void main(String[] args) throws Exception
  {

	  	 if (args.length != 2)
	  	 {
	  		 System.out.println("You should pass as parameter <kafka properties file> <twitter credentials>");
	  		 return; 
	  	 }
	  	 
	     Properties kafkaProperties = new Properties();
	     FileInputStream file;

	     try {
	    	 //load the file handle for main.properties
	    	 file = new FileInputStream(args[0]);
	     }
	     catch (FileNotFoundException e)
	     {
	    	 System.out.println("The file : " + args[0] + " doesnt exist. Please review the path");
	    	 return;
	     }

	     //load all the properties from this file
	     kafkaProperties.load(file);
	     if (!kafkaProperties.containsKey("bootstrap.servers"))
	     {
	    	 System.out.println("Property file: " + args[0] + " must have property metadata.broker.list=<kafkabroker1>:<port>,<kafkabroker2>:<port> defined");
	    	 return;
	     }
	     
	     Properties twitterProperties = new Properties();
	     FileInputStream fileCredentials;
	     try {
	    	 //load the file handle for main.properties
	    	 fileCredentials = new FileInputStream(args[1]);
	     }
	     catch (FileNotFoundException e)
	     {
	    	 System.out.println("The file : " + args[1] + " doesnt exist. Please review the path");
	    	 return;
	     }

	     //load all the properties from this file
	     twitterProperties.load(fileCredentials);
	  
         String consumerKey = twitterProperties.getProperty("consumerKey");
         String consumerSecret = twitterProperties.getProperty("consumerSecret");
         String token= twitterProperties.getProperty("token");
         String secret = twitterProperties.getProperty("secret");
         ArrayList<Location> locations = new ArrayList<Location>();
         
         System.out.println(TwitterApiConsumer.class);
         URL url = TwitterApiConsumer.class.getResource("madridBoundedBoxes.json");
           
         InputStream in = TwitterApiConsumer.class.getResourceAsStream("/madridBoundedBoxes.json"); 
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         StringBuilder stringBuilder = new StringBuilder();
         String line = null;
         while((line = reader.readLine()) != null) {
             stringBuilder.append(line);
         }

         String jsonContent = stringBuilder.toString();
       
         ArrayList<Location> multipleLocations = parseBoundedBoxes(jsonContent);
         
         TwitterGeoStreamKafkaProducer twitterGeoMadridKafkaProducer = new TwitterGeoStreamKafkaProducer(consumerKey, consumerSecret, token, secret, multipleLocations, kafkaProperties);
         final Thread thread = new Thread(twitterGeoMadridKafkaProducer);
         Runtime.getRuntime().addShutdownHook(new Thread() { 
             @Override
             public void run() {
            	 try
            	 {
	                 thread.interrupt();
	                 thread.join();
            	 }
            	 catch (InterruptedException e)
            	 {}
             }
          });
         thread.run();

  }
  
  public static ArrayList<Location> parseBoundedBoxes (String jsonContent) {
	  ArrayList<Location> locations = new ArrayList<Location>();
      try {

          JsonParser jsonParser = new JsonParser();
          JsonObject jo = (JsonObject)jsonParser.parse(jsonContent);
          JsonArray jsonArr = jo.getAsJsonArray("points");
          //jsonArr.
          Gson googleJson = new Gson();
          BoundingBoxTwitter[] boxes = googleJson.fromJson(jsonArr, BoundingBoxTwitter[].class);
          int i = 0;
          while (i < (boxes.length) )
          {
        	  BoundingBoxTwitter box = boxes[i];
        	  Coordinate bottomLeft = new Coordinate(box.getBottom_left_longitude(), box.getBottom_left_latitude());
        	  Coordinate upperRight = new Coordinate(box.getUpper_right_latitude(), box.getUpper_right_latitude());
        	  locations.add(new Location(bottomLeft, upperRight));
        	  i ++;
          }


      } catch (Exception e) {
          e.printStackTrace();
      }
	  

      return locations;
	  
  }
  
}
