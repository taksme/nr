package me.taks.nr;

import me.taks.json.JSONObject;

public class Report {
	private Reports reports;
	public Report(Reports reports) {
		this.reports = reports;
	}
	private Locations getLocations() { return reports.getLocations(); }
	public enum Dir { NONE, UP, DOWN };
	public enum Event { NONE, PASS, ARRIVAL, DEPARTURE };
	
	private Location location;
	private Location next;
	private TimeRange times = new TimeRange(0, 0);
	private String trainId;
	private Dir direction = Dir.NONE;
	private Event event = Event.NONE;
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public void setLocationStanox(String stanox) {
		setLocation(getLocations().byStanox.get(stanox));
	}
	public Location getNext() {
		return next;
	}
	public void setNextStanox(String stanox) {
		this.next = getLocations().byStanox.get(stanox);
	}
	public void setNext(Location next) {
		this.next = next;
	}
	public TimeRange getTimes() {
		return times;
	}
	public void setTimes(TimeRange times) {
		this.times = times;
	}
	public void setTimes(long expected, long actual) {
		this.times = new TimeRange(expected, actual);
	}
	public String getTrainId() {
		return trainId;
	}
	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}
	public Dir getDirection() {
		return direction;
	}
	public void setDirection(Dir direction) {
		this.direction = direction;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public void ready() {
		if (null!=location && null!=times) {
			location.setLastReport(this);
		}
		reports.reportReady(this); //TODO: listener?
	}
	
	public String toJSONString() {
		return String.format(
			"{\"train\":%s,\"dir\":%s,\"evt\":%s," +
				"\"ts\":%s,\"plannedTs\":%s," +
				"\"loc\":%s,\"next\":%s" +
			"}",
			JSONObject.valueToString(trainId),
			JSONObject.valueToString(direction),
			JSONObject.valueToString(event),
			times.getEnd(),
			times.getStart(),
			JSONObject.valueToString(location==null ? "" : location.getStanox()),
			JSONObject.valueToString(next==null ? "" : next.getStanox())
		);
	}
}
