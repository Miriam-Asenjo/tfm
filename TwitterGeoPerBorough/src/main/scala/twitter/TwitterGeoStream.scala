package twitter

import java.util.Properties
import java.io.FileInputStream
import java.io.FileNotFoundException

import com.mongodb.spark._
import com.mongodb.spark.sql._

import kafka.serializer.StringDecoder
import kafka.serializer.DefaultDecoder
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.sql.functions._

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.spark.streaming.dstream.InputDStream;

import com.esri.core.geometry.Point
import com.esri.core.geometry.MultiPoint
import spray.json._
import tfm.FeatureCollection
import tfm.Feature
import tfm.GeoJsonProtocol._
import tfm.RichGeometry


case class GeoTweet (text: String, source: String, country: String, followersCount: Int, friendsCount: Int, 
                     location: Point, var borough: Option[String] = None)


object  TwitterGeoStream {
  
  
    def deserializeTwitter(tweetBytes: Array[Byte]): GeoTweet = {
      try {
			  val reader = new SpecificDatumReader[Tweet](Tweet.SCHEMA$);
			  val binaryDecoder = DecoderFactory.get.binaryDecoder(tweetBytes, null);
			  val tweet = reader.read(null, binaryDecoder); 
			  
			  val coordinates = tweet.getCoordinates().getCoordinates()
        val longitude = coordinates.get(0)
        val latitude = coordinates.get(1)
        val tweetLocation = new Point(longitude.toFloat, latitude.toFloat);

			  
        val geoTweet = GeoTweet(tweet.getText().toString(), tweet.getSource().toString(), 
                                tweet.getPlace().getCountry().toString(), tweet.getUser().getFollowersCount(), 
                                tweet.getUser().getFriendsCount(),  tweetLocation)

			  geoTweet
      } catch {
          case e: Exception => null
        }
    }

  
    def main(args: Array[String]) {
      
      if (args.length != 2)
      {
      	 System.out.println("You should pass as parameteres to this program path to kafka properties file and mongo ip");
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
      val sparkConf = new SparkConf().setAppName("TweetsPerBorough")
                                     .set("spark.mongodb.input.uri",String.format("mongodb://%s/taxiwisedb.events?readPreference=primaryPreferred", mongoIp))
                                     .set("spark.mongodb.output.uri",String.format("mongodb://%s/taxiwisedb.events?readPreference=primaryPreferred", mongoIp))
                                     
      val sc = new SparkContext(sparkConf)
      val sqlContext = SQLContext.getOrCreate(sc) 
      import sqlContext.implicits._

      val ssc = new StreamingContext(sc, Seconds(120))
	  	val geojson = scala.io.Source.fromURL(TwitterGeoStream.getClass.getResource("/todoBarriosMadrid.geojson")).mkString
      val features = geojson.parseJson.convertTo[FeatureCollection]
	  	println ("Num Features: ", features.length)
	  	val bFeatures = ssc.sparkContext.broadcast(features)

      val kafkaParams = Map("metadata.broker.list" -> kafkaProperties.getProperty("bootstrap.servers"))
      // Define which topics to read from
      val topics = Set("tweetsMadrid")

      val tweetsStream = KafkaUtils.createDirectStream[String, Array[Byte], StringDecoder, DefaultDecoder](ssc, kafkaParams, topics)
     
      def localizeTweetInBorough(tweet: GeoTweet): GeoTweet = {
        
        val feature: Option[Feature] = bFeatures.value.find(f => {
          f.geometry.contains(tweet.location)
        })
        if (feature.isDefined)
        {
          tweet.borough = Some(feature.get("label").convertTo[String])
        }
        tweet
      }
  
      try {
      
        tweetsStream.foreachRDD((rdd, time) => {
          
              if (rdd != null) {
                
                  val tweets = rdd.map { case (k, v) => deserializeTwitter(v) }
                  //asign tweets to its Madrid capital neighbourhood
                  val tweetsPerBorough = tweets.map(localizeTweetInBorough).filter { r => r.borough.isDefined }.groupBy( x => x.borough.get).mapValues(_.size)
      
                  val columnNames = Seq("borough", "tweets")
                
                  val currentTimeFunc = udf(() => new java.sql.Timestamp(new java.util.Date().getTime()))
                  val tweetsPerBoroughTimeDf = tweetsPerBorough.toDF(columnNames: _*).withColumn("createdat", currentTimeFunc())
                  
                  MongoSpark.write(tweetsPerBoroughTimeDf.toDF()).option("collection", "numtweetsborough").mode("append").save();
                  
                }
              })
        
      }
      finally 
      {
      
      }

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