package boroughs

import org.apache.spark.{HashPartitioner, Partitioner, SparkConf, SparkContext}
import com.mongodb.spark._
import com.mongodb.spark.sql._
import com.mongodb.spark.config._
import com.mongodb.BasicDBObject
import org.bson.Document
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.UpdateOptions
import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import com.esri.core.geometry.Point
import spray.json._
import tfm.FeatureCollection
import tfm.Feature
import tfm.GeoJsonProtocol._
import tfm.RichGeometry

import java.text.DateFormat
import java.text.SimpleDateFormat


case class CityEvent 
(
    name: String,
    location: Point,
    address: String, 
    totalWeight: Double, 
    var borough: Option[String] = None
)

case class TaxiStandOutEvents
(
    id: Int,
    latitude: Double,
    longitude: Double,
    location: Point,
    outCount: Int,
    var borough: Option[String] = None
    
)

object BusyBorough extends Serializable {
 
  def main(args: Array[String]): Unit = {

        if (args.length != 1)
        {
        	 System.out.println("You should pass as parameteres the mongo ip");
        	 return;
        }    
        
        val mongoIp = args(0)
  
        val sparkConf = new SparkConf()
                                        .setAppName("BusiestBoroughs")
                                        .set("spark.mongodb.input.uri",
                                            String.format("mongodb://%s/taxiwisedb.events?readPreference=primaryPreferred", mongoIp))
                                        .set("spark.mongodb.output.uri",
                                            String.format("mongodb://%s/taxiwisedb.events?readPreference=primaryPreferred", mongoIp))
        //spark context
        val sc = new SparkContext(sparkConf)
        val sqlContext = SQLContext.getOrCreate(sc) 
        sqlContext.setConf("spark.sql.shuffle.partitions", "5")

        
        val now = DateTime.now()
        val convertStringFunc = ((arg:Object) => arg.toString())
        val events = MongoSpark.load(sqlContext).selectExpr(
                                                            "name",
                                                            "place",
                                                            "address",
                                                            "loc",
                                                            "time",
                                                            "type")
                                                            
        val zone = DateTimeZone.forID("Europe/Madrid");
        val lowerBound = new DateTime(zone).minusMinutes(30)
        val upperBound = lowerBound.plusMinutes(90)
        
        val sqlLowerBoundTimestamp = new java.sql.Timestamp(lowerBound.getMillis)
        val sqlUpperBoundTimestamp = new java.sql.Timestamp(upperBound.getMillis)
        val upComingEvents = events.filter(events("time") >= sqlLowerBoundTimestamp).filter(events("time") <= sqlUpperBoundTimestamp)
        
        //println ("NUM EVENTS FILTERED *********************: ", upComingEvents.count())
        val places = sqlContext
                              .loadFromMongoDB(ReadConfig(Map("uri" -> String.format("mongodb://%s/taxiwisedb.places", mongoIp))))
                              .drop("_id")
                              .withColumnRenamed("name", "place_name")
                              .withColumnRenamed("weight", "place_weight")
        //weightevents
        val weightEvents = sqlContext
                              .loadFromMongoDB(ReadConfig(Map("uri" -> String.format("mongodb://%s/taxiwisedb.weightevents", mongoIp))))
                              .drop("_id")
                              .drop("address")
                              .drop("loc")
                              .withColumnRenamed("name", "event_name")
                              .withColumnRenamed("weight", "event_weight")
        
        //join events with places and weightevents 
        val joinedEvents = upComingEvents.join(places, upComingEvents.col("place") <=> places.col("place_name"))
                                          .join(weightEvents, upComingEvents.col("name") <=> weightEvents.col("event_name"),"left_outer")
                                          .map(row => {
                                            val coordinates = row.getStruct(row.fieldIndex("loc")).getList[Double](1)
                                            val location = new Point(coordinates.get(0), coordinates.get(1))
                                            CityEvent(row.getString(row.fieldIndex("name")), location,
                                                  row.getString(row.fieldIndex("address")), row.getInt(row.fieldIndex("place_weight")) +
                                                  row.getDouble(row.fieldIndex("event_weight")))
                                          })


        //val cityEvents = joinedEvents.collect()
                                                  
        val geojson = scala.io.Source.fromURL(getClass.getResource("/todoBarriosMadrid.geojson")).mkString
        val features = geojson.parseJson.convertTo[FeatureCollection]
        
        val bFeatures = sc.broadcast(features)

        def boroughEvent(event: CityEvent): CityEvent = {
              val feature: Option[Feature] = bFeatures.value.find(f => {
                      f.geometry.contains(event.location)
              })
              if (feature.isDefined)
                  event.borough = Some(feature.get("label").convertTo[String])
              event
        }
 
        val eventsPerNeighbourHood = joinedEvents.map(boroughEvent).filter { r => r.borough.isDefined }.groupBy( x => x.borough.get)
        val rankingNeighbourHoodEvents = eventsPerNeighbourHood.mapValues(_.map(_.totalWeight).sum)
        import sqlContext.implicits._
        
        val columnNames = Seq("neighbourdhood", "rank")
        val rankingNeighbourHoodEventsDf = rankingNeighbourHoodEvents.toDF(columnNames: _*)
        rankingNeighbourHoodEventsDf.printSchema()
        
        //neighbourhoods with more tweets last 30 minutes
        val tweetsGeoLowerBound = new java.sql.Timestamp(now.minusMinutes(30).getMillis())
        val convertNumTweetsFunc = udf((numTweets: Int) => numTweets*0.1)
        val tweetsNeighBourdhood = sqlContext
                      .loadFromMongoDB(ReadConfig(Map("uri" -> String.format("mongodb://%s/taxiwisedb.numtweetsborough", mongoIp))))
                      .drop("_id")
                      .withColumn("tweets", convertNumTweetsFunc(col("tweets")))
                      .filter($"createdat" >= tweetsGeoLowerBound)
        
                      
        val aggTweetsPerNeighBourdHood = tweetsNeighBourdhood.select("borough", "tweets").groupBy("borough").sum("tweets").toDF(columnNames: _*)
        aggTweetsPerNeighBourdHood.printSchema()
        
        //neighbourhoods with more exists on the taxi stands within the neighbourhood in the last 20 minutes
        val taxiEventLowerBound = new java.sql.Timestamp(now.minusMinutes(20).getMillis())
 
        val taxiStandOutEvents = sqlContext
                      .loadFromMongoDB(ReadConfig(Map("uri" -> String.format("mongodb://%s/taxiwisedb.taxistandseventsaggregate", mongoIp))))
                      .drop("_id")
                      .filter($"groupedTimestamp" >=  taxiEventLowerBound)
                      .filter("count > 0")
                      .map (x=> 
                        {  
                          val coordinates = x.getStruct(x.fieldIndex("loc")).getList[Double](1)
                          val location = new Point(coordinates.get(0),coordinates.get(1))
                          TaxiStandOutEvents(x.getInt(x.fieldIndex("taxiStandId")), coordinates.get(1), coordinates.get(0), 
                                             location, x.getInt(x.fieldIndex("count")))
                        })
        
        def boroughTaxiStand(taxiStandEvents: TaxiStandOutEvents): TaxiStandOutEvents = {
          val feature: Option[Feature] = bFeatures.value.find(f => {
            f.geometry.contains(taxiStandEvents.location)
          })
          if (feature.isDefined)
            taxiStandEvents.borough = Some(feature.get("label").convertTo[String])
          taxiStandEvents
        }
        
        val taxiStandOutEventsBorough = taxiStandOutEvents.map(boroughTaxiStand)
                                                          .filter { r => r.borough.isDefined }
                                                          .groupBy( x => x.borough.get)
                                                          .mapValues(_.map(_.outCount).sum*0.05)
        
        val currentTime = udf((arg:String) => new java.sql.Timestamp(DateTime.now().getMillis()))
        val taxiStandOutEventsBoroughDf =  taxiStandOutEventsBorough.toDF(columnNames: _*)              
        val rankingBoroughsDf = rankingNeighbourHoodEventsDf.unionAll(aggTweetsPerNeighBourdHood).
                                                          unionAll(taxiStandOutEventsBoroughDf).
                                                          groupBy("neighbourdhood").
                                                          agg(sum("rank").
                                                          alias("rank")).
                                                          withColumn("_id", col("neighbourdhood")).
                                                          withColumn("updatedAt",currentTime(col("neighbourdhood")))
        
        val writeConfig = WriteConfig(Map("uri"-> String.format("mongodb://%s/taxiwisedb.rankingboroughs",mongoIp))) 
        val mongoConnector = MongoConnector(writeConfig.asOptions)
                                                          
        rankingBoroughsDf.foreachPartition(iter => if (iter.nonEmpty) {
            mongoConnector.withCollectionDo(writeConfig, { collection: MongoCollection[Document] =>                        // The underlying Java driver MongoCollection instance.
              iter.foreach(row => {
                                  val filter = new BasicDBObject("_id", row.getString(row.fieldIndex("_id")))
                                  val updateFields = new BasicDBObject()
                                  var updateOptions = new UpdateOptions()
                                  updateOptions.upsert(true)
                                  updateFields.append("rank", row.getDouble(row.fieldIndex("rank")))
                                  updateFields.append("updatedAt", row.getTimestamp(row.fieldIndex("updatedAt")))
                                  val setQuery = new BasicDBObject()
                                  setQuery.append("$set", updateFields)       
                                  collection.updateOne(filter, setQuery, updateOptions)
                
              })
            })
          })
       
        sc.stop()

  }
  
}