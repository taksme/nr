package me.taks.nr;

import me.taks.nr.location.Location;

public class LocViewer {
	public static LocViewer get(Location loc) {
		if (loc==null) return new LocViewer();
		else return new RealLocViewer(loc);
	}

	public String getDesc() { return ""; }

	public String getStanox() { return ""; }

	public String getTla() { return "Unknown"; }
	
	public String getSummary() {
		return getDesc() + " (" + getTla() + ") " + getStanox();
	}
}

class RealLocViewer extends LocViewer {
	private Location loc;
	
	public RealLocViewer(Location loc) {
		this.loc = loc;
	}
	
	public String getDesc() { 
		return ReportViewer.titleCase(loc.getDescription());
	}

	public String getStanox() { 
		return loc.getStanox(); 
	}

	public String getTla() { 
		return loc.getTla(); 
	}
	
}
