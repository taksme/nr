package me.taks.nr;

import me.taks.nr.Locations.Location;
import me.taks.nr.Report.Dir;

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
		return report.getDirection().toString();
	}
	
	public String getEvent() {
		return report.getEvent().toString();
	}
	
	public String getPerformance() {
		return report.getTimes().getMinutesAndQuarters();
	}
	
	public String getHeadcode() {
		return report.getTrainId()!=null ? report.getTrainId().substring(2,6) : "xxxx";
	}
	
	public String getSummary() {
		return getHeadcode() + "\t" + getDirection() + "\t" + getEvent() + "\t" + getPerformance()
				+"\tat "+getLocation()+(getNext()!="" ? "\tfor " + getNext() : "");
	}
}
