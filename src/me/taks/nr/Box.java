package me.taks.nr;

public class Box {
	public Point low, high;
	
	public Box(Point low, Point high) {
		this.low = low;
		this.high = high;
	}
	
	public Point getSize() {
		return new Point(Math.abs(high.easting-low.easting), Math.abs(high.northing-low.northing));
	}
	
	public boolean contains(Point p) {
		return p.gt(low) && p.lt(high);
	}
	
	public void stretchToFit(Point p) {
		if (p.easting == 0 || p.northing == 0) return;
    	if (p.easting>high.easting) high.easting = p.easting;
    	else if (p.easting<low.easting) low.easting = p.easting;
    	if (p.northing>high.northing) high.northing = p.northing;
    	else if (p.northing<low.northing) low.northing = p.northing;
	}
}
