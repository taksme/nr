package me.taks.nr;

import java.util.Hashtable;
import java.util.Vector;


@SuppressWarnings("serial")
public class Locations extends Vector<Location> {
	protected class Index extends Hashtable<String, Location> {}
	protected Index byStanox = new Index();
	protected Index byTiploc = new Index();
	
	public Location getByStanox(String stanox) { 
		return byStanox.get(stanox); 
	}
	
	public Location getByTiploc(String tiploc) { 
		return byTiploc.get(tiploc); 
	}
	
	public Location addNaptanItem(String tiploc, String name, int easting, int northing) {
		Location out = byTiploc.get(tiploc);
		if (out==null) {
			add(out = new Location(this));
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
			add(out = new Location(this));
			out.setTiploc(tiploc);
		}
		out.setStanox(stanox);
		out.setTla(tla);
		out.setShortDescription(shortDescription);
		if (out.getDescription()==null) out.setDescription(description);
		return out;
	}

}
