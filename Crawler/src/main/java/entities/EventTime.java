package entities;

import org.joda.time.DateTime;

public class EventTime
{
	private DateTime startTime;
	private DateTime endTime;
	private int duration;
	
	
	public EventTime (DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}


	
	
	
}