package entities;

import java.util.ArrayList;

import org.joda.time.LocalDateTime;

public class Event
{
	private String name;
	private String address;
	private String place;
	private ArrayList<LocalDateTime> times;
	private Venue venue;
	private EventType type;
	
	public Event (String name, String address, String place, ArrayList<LocalDateTime> times, EventType type)
	{
		this.name = name;
		this.address = address; 
		this.place = place;
		this.times = times;
		this.type = type;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public String getTypeDescription () {
		if (type == EventType.THEATREDANCE)
		{
			return "Teatro-Danza";
		}
		else return "Concierto";
	}


	public ArrayList<LocalDateTime> getTimes() {
		return times;
	}

	public String getPlace() {
		return place;
	}

	public String getName() {
		return name;
	}

	public void setNombre(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Event(String name)
	{
		this.name = name;
	}

}
