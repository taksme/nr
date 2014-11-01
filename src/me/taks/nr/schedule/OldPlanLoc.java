package me.taks.nr.schedule;

import me.taks.nr.location.Location;

public class OldPlanLoc {
	public enum Type { ORIGIN, DESTINATION, PASSING, CALLING, SET_DOWN, PICK_UP }
	
	private Type type;
	private Location loc;
	private short arrival; //half-mins. ie time is (x/120):(x/2):(x*30)
	private short departure; //includes pass
	private short publicArrival;
	private short publicDeparture;
	private String platform;
	private String line;
	private String path;
	private short engineering; //half-mins
	private short pathing; //half-mins
	private short performance; //half-mins

	public OldPlanLoc(Type type, Location loc, short arrival, short departure, 
					short publicArrival, short publicDeparture,
					String platform, String line, String path,
					short engineering, short pathing, short performance
			) {
		this.type = type;
		this.loc = loc;
		this.arrival = arrival;
		this.departure = departure;
		this.publicArrival = publicArrival;
		this.publicDeparture = publicDeparture;
		this.platform = platform;
		this.line = line;
		this.path = path;
		this.engineering = engineering;
		this.pathing = pathing;
		this.performance = performance;
	}
	
	public Type getType() {
		return type;
	}
	public Location getLocation() {
		return loc;
	}
	public short getArrHMs() {
		return arrival;
	}
	public short getDepHMs() {
		return departure;
	}
	public short getPubArrHMs() {
		return publicArrival;
	}
	public short getPubDepHMs() {
		return publicDeparture;
	}
	public String getPlatform() {
		return platform;
	}
	public String getLine() {
		return line;
	}
	public String getPath() {
		return path;
	}
	public short getEngineeringHalfMins() {
		return engineering;
	}
	public short getPathingAllowanceHalfMins() {
		return pathing;
	}
	public short getPerformanceHalfMins() {
		return performance;
	}

}
