package me.taks.nr;

public class ReportViewer {
	public static ReportViewer get(Report report) {
		if (report==null) return new ReportViewer();
		else return new RealReportViewer(report);
	}
	
	public static String titleCase(String in) {
		StringBuffer str = new StringBuffer(in.toLowerCase());
		for (int i=0,j=0; i<str.length() && j>=0; i=(j=str.indexOf(" ", i))+1) {
			if (i<=str.length()) str.setCharAt(i, Character.toUpperCase(str.charAt(i)));
		}
		return str.toString();
	}
	
	public String getLocation() { return "Unknown"; }
	
	public String getNext() { return "Unknown"; }
	
	public String getDirection() { return ""; }
	
	public String getEvent() { return ""; }
	
	public String getPerformance() {
		return getPerformance("early", "late", "on time");
	}
	public String getPerformance(String early, String late, String onTime) {
		return "";
	}
	
	public String getHeadcode() { return "xxxx"; }

	public String getExpectedTime() { return ""; }
	
	public String getSummary() {
		return String.format("%s\t%s%s %s at %s\n%s for %s",
				getExpectedTime(), getDirection(), getEvent(), 
				getPerformance("E", "L", ""), getLocation(),
				getHeadcode(), getNext());
	}
	
	public String getSummaryNoLoc() {
		return String.format("%s\t%s%s %s %s for %s",
				getExpectedTime(), getDirection(), getEvent(), 
				getPerformance("E", "L", ""),
				getHeadcode(), getNext());
	}
}

class RealReportViewer extends ReportViewer {
	private Report report;

	public RealReportViewer(Report report) {
		this.report = report;
	}
	
	public String getLocation() { //should probably be a location renderer
		return LocViewer.get(report.getLocation()).getDesc();
	}
	
	public String getNext() { //should probably be a location renderer
		return LocViewer.get(report.getNext()).getDesc();
	}
	
	public String getDirection() {
		switch (report.getDirection()) {
		case UP: return "⬆";
		case DOWN: return "⬇";
		default: return "";
		}
	}
	
	public String getEvent() {
		switch (report.getEvent()) {
		case ARRIVAL: return "arr";
		case DEPARTURE: return "dep";
		default: return "";
		}
	}
	
	public String getPerformance(String early, String late, String onTime) {
		short diff = HalfMins.diff(report.getActual(), report.getExpected());
		return diff==HalfMins.INVALID ? "?" :
				diff==0 ? onTime :
				HalfMins.toString(diff) + (diff>0 ? late : early);
	}
	
	public String getHeadcode() {
		return report.getTrainId()!=null ? report.getTrainId().substring(2,6) : "xxxx";
	}
	
	public String getExpectedTime() {
		return HalfMins.toString(report.getExpected());
	}
}
