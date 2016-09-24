package tfm

import kafka.serializer.StringDecoder
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.kafka.KafkaUtils
import com.mongodb.spark._
import org.apache.spark.sql.functions._
import com.datastax.spark.connector._
import com.datastax.spark.connector.mapper.DefaultColumnMapper
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import com.mongodb.spark._
import com.mongodb.spark.sql._
import com.mongodb.spark.config._
import org.bson.Document;

import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.google.gson.JsonDeserializer
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonElement
import org.apache.spark.rdd.RDD

import java.util.Arrays
import java.util.HashMap
import java.util.Properties
import java.io.FileInputStream
import java.io.FileNotFoundException

case class TaxiStand (taxiStandId: Int, address: String, latitude: Double, longitude: Double, numPlaces: Int)

case class TaxiStandEvent(taxiStandId: Int, eventTimestamp: DateTime, inEvent: Boolean)

case class TaxiStandGroupedEvents(taxiStandId: Int, address: String, groupedTimestamp: DateTime, numEvents: Int, latitude: Double, longitude: Double, numPlaces:Int)

object TaxiStandEvent { 
    implicit object Mapper extends DefaultColumnMapper[TaxiStandEvent](
        scala.collection.immutable.Map("Id" -> "id", "TimeStamp" -> "timeStamp")) 
}

object Joda {
    implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)
}

object Program extends App
{
  
  override def main (args: Array[String])
  {
    
    if (args.length != 2)
	  {
	  		 System.out.println("You should pass as parameter to this program path to kafka properties file and mongo ip");
	  		 return;
	  }
    
    val kafkaProperties = getKafkaConfiguration(args(0))
    if (kafkaProperties == null)
    {
	    	 println("File", args(0)," has not been found")
	    	 return      
    }
	  if (!kafkaProperties.containsKey("bootstrap.servers"))
	  {
	    	 println("Property file ", args(0)," must have property metadata.broker.list=<kafkabroker1>:<port>,<kafkabroker2>:<port> defined")
	    	 return
	  }
    
    val mongoIp = args(1)
    val sparkConf = new SparkConf().setAppName("TaxiStandsEventsConsumer")
                                   .set("spark.mongodb.input.uri",String.format("mongodb://%s/taxiwisedb.events?readPreference=primaryPreferred", mongoIp))
                                   .set("spark.mongodb.output.uri",String.format("mongodb://%s/taxiwisedb.taxistandseventsaggregate",mongoIp))
                                   
    val sc = new SparkContext(sparkConf)
    val sqlContext = SQLContext.getOrCreate(sc) 
    import sqlContext.implicits._
    import Joda._
    
    //read from mongo taxi stands collection to then use geolocation when doing aggregation and store it in the real time view
    //to accomplish use case given a taxi show the nearest stands with more activity
    val taxiStands = sqlContext.loadFromMongoDB(ReadConfig(Map("uri" -> String.format("mongodb://%s/taxiwisedb.taxistands", mongoIp))))
                           .map (x=> (x.getInt(x.fieldIndex("_id")), 
                                      TaxiStand(x.getInt(x.fieldIndex("_id")), x.getString(x.fieldIndex("address")),x.getDouble(x.fieldIndex("latitude")),x.getDouble(x.fieldIndex("longitude")),x.getInt(x.fieldIndex("numplaces"))))).collect()    
              
    val taxiStandsBroad = sc.broadcast(taxiStands.toMap)
    
    
    val ssc = new StreamingContext(sc, Seconds(120))
    val kafkaParams = Map("metadata.broker.list" -> kafkaProperties.getProperty("bootstrap.servers"))
    val topics = Set("taxi-out-stand", "taxi-in-stand")
    val writeConfig = WriteConfig(sc) 
    val taxiStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)
    
    taxiStream.foreachRDD((rdd, time) => {
      
      if (rdd != null) {

        val taxiEvents = rdd.map { case(k,v) => 
        {
              val gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(classOf[TaxiStandEvent], TaxiEventDeserializer).setPrettyPrinting().create()
              gsonBuilder.fromJson(v, classOf[TaxiStandEvent])}
        }
        

        //in events (taxis arriving to taxi stand)
        val taxiInEvents = taxiEvents.filter (x => x.inEvent == true)
        taxiInEvents.cache()
        val taxiGrInEvents:RDD[org.bson.Document] = taxiInEvents
                                      .groupBy(x=> x.taxiStandId)
                                      .map { case (name, c) =>
                                                              (name, c.map(_.eventTimestamp).max, c.size)
                                      }
                                      .map(x=> {
                                        val stand:TaxiStand = taxiStandsBroad.value.get(x._1).get
                                        val document = new Document()
                                    		document.append("taxiStandId",stand.taxiStandId)
                                    		document.append("address", stand.address)
                                    		document.append("groupedTimestamp", new java.sql.Timestamp(x._2.getMillis()))
                                    		document.append("numPlaces", stand.numPlaces)
                                    		document.append("count", x._3)
                                    		document.append("loc", new Document("type", "Point").append("coordinates",
                                    				Arrays.asList(stand.longitude, stand.latitude)))
                                      })
                                      
        //out events taxis leaving taxi stand                              
        val taxiOutEvents = taxiEvents.filter (x => x.inEvent == false)   
        taxiOutEvents.cache()

        val taxiGrOutEvents:RDD[org.bson.Document] = taxiOutEvents
                                      .groupBy(x=> x.taxiStandId)
                                      .map { case (name, c) =>
                                                              (name, c.map(_.eventTimestamp).max, c.size)
                                      }
                                      .map(x=> {
                                        val stand:TaxiStand = taxiStandsBroad.value.get(x._1).get
                                        val document = new org.bson.Document()
                                    		document.append("taxiStandId",stand.taxiStandId)
                                    		document.append("address", stand.address)
                                    	  document.append("groupedTimestamp", new java.sql.Timestamp(x._2.getMillis()))
                                    		document.append("numPlaces", stand.numPlaces)
                                    		document.append("count", x._3*(-1))
                                    		document.append("loc", new Document("type", "Point").append("coordinates",
                                    				Arrays.asList(stand.longitude, stand.latitude)))
                                      })
                                      

        MongoSpark.save(taxiGrInEvents, writeConfig);
        MongoSpark.save(taxiGrOutEvents, writeConfig);

      }
    })
    
    ssc.start()             // Start the computation
    ssc.awaitTermination()  // Wait for the computation to terminate

  }


  def getKafkaConfiguration(filePath: String) : Properties  = {
	     var kafkaProperties = new Properties()
	     
	     try {
	    	 //load the file handle for main.properties
	    	 val file = new FileInputStream(filePath)
	    	 kafkaProperties.load(file)
	    	 kafkaProperties
	     }
	     catch
	     {
	       case ioe: java.io.FileNotFoundException => {
	         println("The file ", filePath, " doesnt exist. Please review the path")
	         null
	       }
         case e: Exception => null
	    	 
	     }
  }
}