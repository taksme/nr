package me.taks.nr;

import java.util.Hashtable;
import java.util.Vector;

import me.taks.json.JSONObject;

public class Locations extends Vector<Locations.Location> {
	public class Location {
		public String getStanox() {
			return stanox;
		}
		
		public void setStanox(String stanox) {
			if (null!=this.stanox) {
				byStanox.remove(this.stanox);
			}
			this.stanox = stanox;
			byStanox.put(this.stanox, this);
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
				byTiploc.remove(this.tiploc);
			}
			this.tiploc = tiploc;
			byTiploc.put(tiploc, this);
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
				JSONObject.valueToString(pos==null ? 0 : pos.easting),
				JSONObject.valueToString(pos==null ? 0 : pos.northing),
				lastReport==null ? "null" : lastReport.toJSONString()
			);
		}
	}
	@SuppressWarnings("serial")
	public class ByStanox extends Hashtable<String, Location> {}
	public ByStanox byStanox = new ByStanox();
	@SuppressWarnings("serial")
	public class ByTiploc extends Hashtable<String, Location> {}
	public ByTiploc byTiploc = new ByTiploc();
	
	public Location addNaptanItem(String tiploc, String name, int easting, int northing) {
		Location out = byTiploc.get(tiploc);
		if (out==null) {
			add(out = new Location());
			out.setTiploc(tiploc);
		}
		//if (out.getDescription()==null) 
			out.setDescription(name.replace(" Rail Station", ""));
		out.setLocation(northing, easting);
		return out;
	}
	
	public Location addCorpusItem(String stanox, String tiploc, String tla, 
									String shortDescription, String description) {
		Location out = byTiploc.get(tiploc);
		if (out==null) {
			add(out = new Location());
			out.setTiploc(tiploc);
		}
		out.setStanox(stanox);
		out.setTla(tla);
		out.setShortDescription(shortDescription);
		if (out.getDescription()==null) out.setDescription(description);
		return out;
	}

}
