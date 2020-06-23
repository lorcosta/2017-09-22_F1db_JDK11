package it.polito.tdp.formulaone.model;

public class Event implements Comparable<Event>{
	public enum Type{
		TAGLIO_TRAGUARDO,
		PIT_STOP
	}
	private Type type;
	private LapTime time;
	private Driver driver;
	/**
	 * @param type
	 * @param time
	 * @param driver
	 */
	public Event(Type type, LapTime time, Driver driver) {
		super();
		this.type = type;
		this.time = time;
		this.driver = driver;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public LapTime getTime() {
		return time;
	}
	public void setTime(LapTime time) {
		this.time = time;
	}
	public Driver getDriver() {
		return driver;
	}
	public void setDriver(Driver driver) {
		this.driver = driver;
	}
	@Override
	public int compareTo(Event other) {
		if(this.time.getLap().equals(other.getTime().getLap())) {
			return this.time.getMiliseconds().compareTo(other.getTime().getMiliseconds());
		}else {
			return this.time.getLap().compareTo(other.getTime().getLap());
		}
	}
	@Override
	public String toString() {
		return type+" , "+driver;
	}
	
}
