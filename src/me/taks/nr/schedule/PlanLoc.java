package me.taks.nr.schedule;

import me.taks.nr.location.Location;

public class PlanLoc {
	public enum Type { ORIGIN, DESTINATION, PASSING, CALLING, SET_DOWN, PICK_UP }
	
	private Type type;
	private Location loc;
	private int arrival; //half-mins. ie time is (x/120):(x/2):(x*30)
	private int departure; //includes pass
	private int publicArrival;
	private int publicDeparture;
	private String platform;
	private String line;
	private String path;
	private int engineering; //half-mins
	private int pathing; //half-mins
	private int performance; //half-mins

	public Type getType() {
		return type;
	}
	public Location getLocation() {
		return loc;
	}
	public int getArrHMs() {
		return arrival;
	}
	public int getDepHMs() {
		return departure;
	}
	public int getPubArrHMs() {
		return publicArrival;
	}
	public int getPubDepHMs() {
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
	public int getEngineeringHalfMins() {
		return engineering;
	}
	public int getPathingAllowanceHalfMins() {
		return pathing;
	}
	public int getPerformanceHalfMins() {
		return performance;
	}
	
	public static class Builder {
		private PlanLoc planLoc = new PlanLoc();
		
		public Builder setType(Type type) {
			planLoc.type = type;
			return this;
		}
		
		public Builder setLoc(Location loc) {
			planLoc.loc = loc;
			return this;
		}
		
		public Builder setArrival(int arrival) {
			planLoc.arrival = arrival;
			return this;
		}
		
		public Builder setDeparture(int departure) {
			planLoc.departure = departure;
			return this;
		}
		
		public Builder setPublicArrival(int publicArrival) {
			planLoc.publicArrival = publicArrival;
			return this;
		}
		
		public Builder setPublicDeparture(int publicDeparture) {
			planLoc.publicDeparture = publicDeparture;
			return this;
		}
		
		public Builder setPlatform(String platform) {
			planLoc.platform = platform;
			return this;
		}
		
		public Builder setLine(String line) {
			planLoc.line = line;
			return this;
		}
		
		public Builder setPath(String path) {
			planLoc.path = path;
			return this;
		}
		
		public Builder setEngineering(int engineering) {
			planLoc.engineering = engineering;
			return this;
		}
		
		public Builder setPathing(int pathing) {
			planLoc.pathing = pathing;
			return this;
		}
		
		public Builder setPerformance(int performance) {
			planLoc.performance = performance;
			return this;
		}
		
		public PlanLoc build() {
			return planLoc;
		}

	}
	
}
