package tfm

import java.lang.reflect.Type
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import com.google.gson.stream.JsonReader
import com.google.gson.JsonDeserializer
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonElement
import org.joda.time.format.ISODateTimeFormat

object TaxiEventDeserializer extends JsonDeserializer[TaxiStandEvent] {
  override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TaxiStandEvent = {
    val res = json match {
      case o: JsonObject
        => 
          val id = o.get("id").getAsInt()
          val timeString = o.get("dateTime").getAsString() 
          val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
          
          val timeStamp = formatter.parseDateTime(timeString);
          val inEvent = o.get("inOut").getAsBoolean();
          TaxiStandEvent (id, timeStamp, inEvent)
       
      case _ => null
    }

    res
  }
}






  
  
  
  
