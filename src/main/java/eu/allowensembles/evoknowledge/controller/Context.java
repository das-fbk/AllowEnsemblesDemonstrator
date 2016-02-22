package eu.allowensembles.evoknowledge.controller;

public class Context {

	public enum Weekday {
		
		Monday("Monday"),
		
		Tuesday("Tuesday"),
		
		Wednesday("Wednesday"),
		
		Thursday("Thursday"),
		
		Friday("Friday"),
		
		Saturday("Saturday"),
		
		Sunday("Sunday");
		
		private String prettyPrint;
		
		private Weekday(String prettyPrint) {
			this.prettyPrint = prettyPrint;
		}
		
		public String toString() {
			return prettyPrint;
		}
	}
	
	public enum TimeOfDay {
		
		EarlyMorning("Early morning"),
		
		Morning("Morning"),
		
		EarlyAfternoon("Early afternoon"),
		
		Afternoon("Afternoon"),
		
		Evening("Evening"),
		
		Night("Night");
				
		private String prettyPrint;
		
		private TimeOfDay(String prettyPrint) {
			this.prettyPrint = prettyPrint;
		}
		
		public String toString() {
			return prettyPrint;
		}
	}
	
	public enum Weather {
		
		Sun("Sunny"),
		
		Rain("Rainy"),
		
		Snow("Snowy");
				
		private String prettyPrint;
		
		private Weather(String prettyPrint) {
			this.prettyPrint = prettyPrint;
		}
		
		public String toString() {
			return prettyPrint;
		}
	}

	public static Weekday WEEKDAYS[] = { Weekday.Monday, Weekday.Tuesday, Weekday.Wednesday, 
		Weekday.Thursday, Weekday.Friday, Weekday.Saturday, Weekday.Sunday };
	
	public static TimeOfDay TIME_OF_DAY[] = { TimeOfDay.EarlyMorning, TimeOfDay.Morning, 
		TimeOfDay.EarlyAfternoon, TimeOfDay.Afternoon, TimeOfDay.Evening, TimeOfDay.Night };
	
	public static Weather WEATHER[] = { Weather.Sun, Weather.Rain, Weather.Snow }; 
	
	private Weekday weekday;
	private TimeOfDay timeOfDay;
	private Weather weather;
	
	public Context() {
		weekday = Weekday.Monday;
		timeOfDay = TimeOfDay.EarlyMorning;
		weather = Weather.Sun;
	}
	
	public Weekday getWeekday() {
		return weekday;
	}
	
	public void setWeekday(Weekday weekday) {
		this.weekday = weekday;
	}
	
	public TimeOfDay getTimeOfDay() {
		return timeOfDay;
	}
	
	public void setTimeOfDay(TimeOfDay timeOfDay) {
		this.timeOfDay = timeOfDay;
	}
	
	public Weather getWeather() {
		return weather;
	}
	
	public void setWeather(Weather weather) {
		this.weather = weather;
	}
}
