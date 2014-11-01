package me.taks.nr;

import javax.swing.DefaultListModel;

import me.taks.nr.location.Locations;

public class Reports extends DefaultListModel<Report> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Locations locations;
	
	public Locations getLocations() {
		return locations;
	}

	public Reports(Locations locations) {
		this.locations = locations;
	}
	
	public Report NewReport() {
		return new Report(this);
	}
	
	public void reportReady(Report report){
		this.insertElementAt(report, 0);
		if (size()>100) setSize(100);
	}
}
