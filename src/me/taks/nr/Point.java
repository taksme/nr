package me.taks.nr;

public class Point {
	public int easting, northing;
	public Point(int easting, int northing) {
		this.easting = easting;
		this.northing = northing;
	}
	
	public boolean equals(Point p) {
		return easting == p.easting && northing == p.northing;
	}
	
	public boolean gt(Point p) {
		return easting > p.easting && northing > p.northing;
	}
	
	public boolean lt(Point p) {
		return easting < p.easting && northing < p.northing;
	}
}
