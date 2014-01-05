package me.taks.nr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportViewer {
	private Report report;
	public ReportViewer(Report report) {
		this.report = report;
	}
	
	public String getLocation() { //should probably be a location renderer
		Location l = report.getLocation();
		return l!=null ? l.getDescription() : "";
	}
	
	public String getNext() { //should probably be a location renderer
		Location l = report.getNext();
		return l!=null ? l.getDescription() : "";
	}
	
	public String getDirection() {
		switch (report.getDirection()) {
		case UP: return "⬆";
		case DOWN: return "⬇";
		default: return "";
		}
	}
	
	public String getEvent() {
		return report.getEvent().toString();
	}
	
	public String getPerformance() {
		return getPerformance("early", "late", "on time");
	}
	public String getPerformance(String early, String late, String onTime) {
		return report.getTimes().getMinutesAndQuarters(early, late, onTime);
	}
	
	public String getHeadcode() {
		return report.getTrainId()!=null ? report.getTrainId().substring(2,6) : "xxxx";
	}
	
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.US);
	public String getExpectedTime() {
		return dateFormatter.format(new Date(report.getTimes().getStart()));
	}
	
	public String getSummary() {
		return String.format("%s\t%s%s %s at %s\n%s for %s",
				getExpectedTime(), getDirection(), getEvent(), 
				getPerformance("E", "L", ""), getLocation(),
				getHeadcode(), getNext());
	}
}
