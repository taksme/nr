package me.taks.nr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
		long diff = report.getRealDelay();
		
		return diff==Long.MAX_VALUE ? "?" :
				diff==0 ? onTime :
				longToTIme(Math.abs(diff));
	}
	
	public String getHeadcode() {
		return report.getTrainId()!=null ? report.getTrainId().substring(2,6) : "xxxx";
	}
	
	private static SimpleDateFormat sdf;
	static {
		sdf = new SimpleDateFormat("kk:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/London"));
	}
	
	private static String longToTIme(long time) {
		return sdf.format(new Date(time)) + (time%1000>0 ? "½" : "");
	}
	
	public String getExpectedTime() {
		return longToTIme(report.getExpected());
	}
}
