package crawler;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

public final class EventParserUtils {

	//L a S a las 22 h
	//X a las 20 h
	private static final String _horarios1 = "(?<month>((Julio)|(Agosto)|(Septiembre)|(Octubre)|(Noviembre)|(Diciembre)|(Enero)|(Febrero)|(Marzo)|(Abril)|(Mayo)|(Junio))): (?<days>(?:[L|M|X|J|V|S|D]\\s?[,|y]?\\d{0,2}\\s?){1,}) a las (?<time>(?:\\d{1,2}\\.?\\d{1,2}?\\s?[,|y]?\\s?){1,}) h";
	private  static final Pattern _patternHorarios1 = Pattern.compile(_horarios1);
	
	//X,J,V a las 18 h
	//V y S a las 22 h	
	private static final String _horarios2 = "(?i)(?<days>(?:[L|M|X|J|V|S|D]\\s?\\d{0,2}\\s?[,|y]?\\s?){1,}) de (?<month>((Julio)|(Agosto)|(Septiembre)|(Octubre)|(Noviembre)|(Diciembre)|(Enero)|(Febrero)|(Marzo)|(Abril)|(Mayo)|(Junio))) a las (?<time>(?:\\d{1,2}\\.?\\d{1,2}?\\s?[,|y]?\\s?){1,}) h";
	private  static final Pattern _patternHorarios2 = Pattern.compile(_horarios2);
	
	private static final String _horarios3 = "(?<days>(?:[L|M|X|J|V|S|D]\\s?\\d{0,2}\\s?[,|y|a]?\\s?){1,}) a las (?<time>(?:\\d{1,2}[.|:]?\\d{1,2}?\\s?[,|y]?\\s?){1,}) h";
	private  static final Pattern _patternHorarios3 = Pattern.compile(_horarios3);
	
	private static final String _horariosConcerts1 = "^(?<time>(?:\\d{1,2}[.|:]?\\d{1,2}?\\s?[,|y]?\\s?){1,}) h";
    private static final Pattern _patternHorariosConcerts1 = Pattern.compile(_horariosConcerts1); 
	
	private static ArrayList<String> daysList = new ArrayList<String>(Arrays.asList(new String[] {"L","M","X","J","V","S","D"}));
	private static ArrayList<String> monthList = new ArrayList<String>(Arrays.asList(new String[] {"enero","febrero","marzo","abril","mayo",
			"junio","julio","agosto","septiembre","octubre","noviembre","diciembre"}));	
	
	private static final String _daysWithInMonth = "(?<day>[L|M|X|J|V|S|D])\\s?(?<numDay>\\d{1,2})?";
	private static final Pattern _patternDaysWithInMonth = Pattern.compile(_daysWithInMonth);
	
	
	public static ArrayList<LocalDateTime> ParseConcertTimetable (String horario, String start, String end)
	{
		ArrayList<LocalDateTime> eventTimes = new ArrayList<LocalDateTime>();
		if (horario == null || horario.length()==1)
			return eventTimes;
		
		Matcher matcherHorariosConcerts = _patternHorariosConcerts1.matcher(horario);
		LocalDate startDate = LocalDate.parse(start, DateTimeFormat.forPattern("dd/MM/yyyy"));
		LocalDate endDate = startDate;
		if (end != null)
			endDate = LocalDate.parse(end, DateTimeFormat.forPattern("dd/MM/yyyy"));
    	while (matcherHorariosConcerts.find())
    	{
    		ArrayList<Map.Entry<Integer, Integer>> horarios = EventParserUtils.getHorarios(matcherHorariosConcerts.group("time"));
    		LocalDate dateEvent = startDate;
    		while (dateEvent.compareTo(endDate) <= 0)
    		{
    			for (Map.Entry<Integer,Integer> timeInfo: horarios)
    				eventTimes.addAll(EventParserUtils.getEventTime(dateEvent.getMonthOfYear(), dateEvent.getDayOfMonth(), timeInfo.getKey(),timeInfo.getValue()));
    			
    			dateEvent = dateEvent.plusDays(1);
    		}
    	}
		
    	return eventTimes;
			
	}

	public static ArrayList<LocalDateTime> ParseTheaterTimetable (String horario, String startDate, String endDate) {
		
		boolean parsedTimetable = false;
    	Matcher matcherHorarios1 = _patternHorarios1.matcher(horario);
    	ArrayList<LocalDateTime> eventTimes = new ArrayList<LocalDateTime>();
    	while (matcherHorarios1.find())
    	{
    		String month = matcherHorarios1.group("month");
    		eventTimes.addAll(EventParserUtils.parseDaysWithinMonths(startDate, matcherHorarios1.group("days"), matcherHorarios1.group("time"), month));
    		parsedTimetable = true;
    	}
    	
    	Matcher matcherHorarios2 = _patternHorarios2.matcher(horario);
    	if (!parsedTimetable)
    	{
	    	while (matcherHorarios2.find())
	    	{
	    		parsedTimetable=true;
	    		eventTimes.addAll(EventParserUtils.parseDaysWithinMonths(startDate, matcherHorarios2.group("days"), matcherHorarios2.group("time"), matcherHorarios2.group("month")));
	    	}
    	}
    	
    	Matcher matcherHorarios3 = _patternHorarios3.matcher(horario);
    	if (!parsedTimetable)
    	{
	    	while (matcherHorarios3.find())
	    	{
	    		parsedTimetable=true;
	    		String [] days = matcherHorarios3.group("days").replace("De ", "").split("(,)|(y)");
	    		String [] daysInterval = matcherHorarios3.group("days").replace("De ", "").split("a");
	    		ArrayList<Map.Entry<Integer, Integer>> horarios = EventParserUtils.getHorarios(matcherHorarios3.group("time"));
	    		
	    		if (daysInterval.length > 1) 
	    		{
	    			int startDay = daysList.indexOf(daysInterval[0].trim()) + 1;
	    			int endDay = daysList.indexOf(daysInterval[1].trim()) + 1;

	    			for (int i = startDay; (i <= endDay && i >= 1); i++)
	    			{
	    				for (Map.Entry<Integer,Integer> timeInfo: horarios)
	    					eventTimes.addAll(EventParserUtils.getWeekDayBetweenDatesRange(startDate,endDate, i, timeInfo.getKey(), timeInfo.getValue()));
	    			}
	    		}else {
	    			for (int index=0; index < days.length; index ++)
	    			{
	    				int day = daysList.indexOf(days[index].trim()) + 1;

	    				if (day > 0)
	    				{
	    					for (Map.Entry<Integer,Integer> timeInfo: horarios)
	    						eventTimes.addAll(EventParserUtils.getWeekDayBetweenDatesRange(startDate,endDate, day, timeInfo.getKey(), timeInfo.getValue()));
	    				}
	    				else {
	    					LocalDate start = LocalDate.parse(startDate, DateTimeFormat.forPattern("dd/MM/yyyy"));
	    					String monthName = monthList.get(start.getMonthOfYear()-1);
	    					eventTimes.addAll(EventParserUtils.parseDaysWithinMonths(startDate, matcherHorarios3.group("days"), matcherHorarios3.group("time"), monthName));
	    				}
	    			}
	    		}
	    		
	    	}
    	}

    	
    	if (parsedTimetable == false || eventTimes.size() == 0)
    	{
    		
    	}
    	return eventTimes;
	}
	
	
	private static ArrayList<LocalDateTime> parseDaysWithinMonths (String startDate, String daysGroup, String timeGroup, String month)
	{
		ArrayList<LocalDateTime> eventTimes = new ArrayList<LocalDateTime>();
		Matcher matcherDaysWithInMonth = _patternDaysWithInMonth.matcher(daysGroup);
		while (matcherDaysWithInMonth.find())
		{
			int monthNum = monthList.indexOf(month.toLowerCase().trim()) + 1;
    		ArrayList<Map.Entry<Integer, Integer>> horarios = EventParserUtils.getHorarios(timeGroup);
			if (matcherDaysWithInMonth.group("numDay")!= null)
			{
    			int numDay = Integer.parseInt(matcherDaysWithInMonth.group("numDay"));
    			for (Map.Entry<Integer,Integer> timeInfo: horarios)
    				eventTimes.addAll(EventParserUtils.getEventTime(monthNum, numDay, timeInfo.getKey(),timeInfo.getValue()));
			}
			else 
			{
				int day = daysList.indexOf(matcherDaysWithInMonth.group("day").trim()) + 1;
				for (Map.Entry<Integer,Integer> timeInfo: horarios)
				{
					eventTimes.addAll(EventParserUtils.getWeekDayBetweenDatesRange(startDate, monthNum, day, timeInfo.getKey(), timeInfo.getValue())); 
				}
			}
			
		}
		
		return eventTimes;
	}
	
	private static ArrayList<Map.Entry<Integer, Integer>> getHorarios(String horariosGroup) {
		ArrayList<Map.Entry<Integer, Integer>> horarios = new ArrayList<Map.Entry<Integer, Integer>>();
		String [] horariosDetails = horariosGroup.split("y");
		for (String horarioInfo: horariosDetails)
		{
			String [] timeInterval = horarioInfo.split("(\\.)|(:)");
			int hour = Integer.parseInt(timeInterval[0].trim());
			int minute = timeInterval.length > 1 ? Integer.parseInt(timeInterval[1].trim()):0;
			horarios.add(new AbstractMap.SimpleEntry(hour,minute));
		}		
		
		return horarios;
	}
	
	public static ArrayList<LocalDateTime> getWeekDayBetweenDatesRange(String start, String end, int numDayOfWeek, int hour, int minute) {
		ArrayList<LocalDateTime> eventDates = new ArrayList<LocalDateTime>();
		LocalDate startDate = LocalDate.parse(start, DateTimeFormat.forPattern("dd/MM/yyyy"));
		LocalDate endDate = null;
		if (end == null || end.trim() == "")
		{
			DateTime today = DateTime.now();
			if (startDate.getMonthOfYear() > today.getMonthOfYear() || startDate.getYear() > today.getYear())
				endDate = new LocalDate(startDate.getYear(),startDate.getMonthOfYear(), startDate.dayOfMonth().getMaximumValue());
			else
				endDate = new LocalDate(today.getYear(), today.getMonthOfYear(), today.dayOfMonth().getMaximumValue());
		}
		else 
		{
			endDate = LocalDate.parse(end, DateTimeFormat.forPattern("dd/MM/yyyy"));
		}
		
		LocalDate thisNumDayOfWeek = startDate.withDayOfWeek(numDayOfWeek);

		if (startDate.isAfter(thisNumDayOfWeek )) {
		    startDate = thisNumDayOfWeek.plusWeeks(1); // start on next monday
		} else {
		    startDate = thisNumDayOfWeek; // start on this monday
		}

		while (startDate.isBefore(endDate)) {
		    startDate = startDate.plusWeeks(1);
		    LocalDateTime eventTime = new LocalDateTime(startDate.getYear(), startDate.getMonthOfYear(), startDate.getDayOfMonth(), hour, minute);
		    eventDates.add(eventTime);
		}
		
		return eventDates;
	}
	
	
	public static ArrayList<LocalDateTime> getWeekDayBetweenDatesRange(String start, int month, int numDayOfWeek, int hour, int minute) {
		ArrayList<LocalDateTime> eventDates = new ArrayList<LocalDateTime>();
		LocalDate startDate = LocalDate.parse(start, DateTimeFormat.forPattern("dd/MM/yyyy"));
		LocalDate endDate = null;
		if (startDate.getMonthOfYear() == month)
		{
			endDate = new LocalDate(startDate.getYear(), startDate.getMonthOfYear(), startDate.dayOfMonth().getMaximumValue());
		}else 
		{
			int year = startDate.getYear();
			if (startDate.getMonthOfYear() > month)
				year = year + 1;
			
			startDate = new LocalDate(year, month, 1);
			endDate = new LocalDate(year, month, startDate.dayOfMonth().getMaximumValue()); 
		}
		
		LocalDate thisNumDayOfWeek = startDate.withDayOfWeek(numDayOfWeek);

		if (startDate.isAfter(thisNumDayOfWeek )) {
		    startDate = thisNumDayOfWeek.plusWeeks(1); // start on next monday
		} else {
		    startDate = thisNumDayOfWeek; // start on this monday
		}

		while (startDate.isBefore(endDate)) {
		    startDate = startDate.plusWeeks(1);
		    LocalDateTime eventTime = new LocalDateTime(startDate.getYear(), startDate.getMonthOfYear(), startDate.getDayOfMonth(), hour, minute);
		    eventDates.add(eventTime);
		}
		
		return eventDates;
		
	}
	
	public static ArrayList<LocalDateTime> getEventTime (int month, int numDay, int hour, int minute)
	{
		ArrayList<LocalDateTime> eventDates = new ArrayList<LocalDateTime>();
		DateTime today = DateTime.now();
		int year = today.getYear();
		if (today.getMonthOfYear() > month)
			year = year + 1;
		LocalDateTime eventTime = new LocalDateTime(year, month, numDay, hour, minute);
		eventDates.add(eventTime);
		return eventDates;
		
	}
}
