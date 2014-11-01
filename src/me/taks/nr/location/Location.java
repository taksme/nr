package me.taks.nr.location;

import me.taks.json.JSONObject;
import me.taks.nr.Point;
import me.taks.nr.Report;

public class Location {
	/**
	 * 
	 */
	private final Locations locations;

	/**
	 * @param locations
	 */
	Location(Locations locations) {
		this.locations = locations;
	}

	public String getStanox() {
		return stanox;
	}
	
	public void setStanox(String stanox) {
		if (null!=this.stanox) {
			this.locations.byStanox.remove(this.stanox);
		}
		this.stanox = stanox;
		this.locations.byStanox.put(this.stanox, this);
	}
	
	public String getTla() {
		return tla;
	}
	
	public void setTla(String tla) {
		this.tla = tla;
	}
	
	public String getTiploc() {
		return tiploc;
	}
	
	public String getAtco() {
		return "9100"+tiploc;
	}
	
	public void setTiploc(String tiploc) {
		if (null!=this.tiploc) {
			this.locations.byTiploc.remove(this.tiploc);
		}
		this.tiploc = tiploc;
		this.locations.byTiploc.put(tiploc, this);
	}
	
	public String getShortDescription() {
		return shortDescription;
	}
	
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Point getLocation() {
		return pos;
	}
	
	public void setLocation(int northing, int easting) {
		pos = new Point(easting, northing);
	}
	
	public Report getLastReport() {
		return lastReport;
	}

	public void setLastReport(Report lastReport) {
		this.lastReport = lastReport;
	}

	private String stanox;
	private String tla;
	private String tiploc;
	private String shortDescription;
	private String description;
	private Point pos;
	private Report lastReport;

	public String toJSONString() {
		return String.format(
			"{\"id\":%s,\"tla\":%s,\"tiploc\":%s,\"sDesc\":%s,\"desc\":%s," +
				"\"e\":%s,\"n\":%s,\"report\":%s" +
			"}",
			JSONObject.valueToString(stanox),
			JSONObject.valueToString(tla),
			JSONObject.valueToString(tiploc),
			JSONObject.valueToString(shortDescription),
			JSONObject.valueToString(description),
			pos==null ? 0 : pos.easting,
			pos==null ? 0 : pos.northing,
			lastReport==null ? null : lastReport.toJSONString()
		);
	}
}