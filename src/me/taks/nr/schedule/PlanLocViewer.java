package me.taks.nr.schedule;

import me.taks.nr.LocViewer;

public class PlanLocViewer {
	public static PlanLocViewer get(PlanLoc planLoc) {
		if (planLoc == null) return new PlanLocViewer();
		else return new RealPlanLocViewer(planLoc);
	}

	public String getLocation() { return "Unknown Location"; }
	
	public static String hmsToHHMMF(int hm) {
		if (hm<0) return "";
		else return String.format("%02d:%02d%s", hm/120, (hm%120/2), hm%2==1?"½":"");
	}
	
	public static String firstBracketSecondOrBest(int first, int second) {
		return first<0 ? hmsToHHMMF(second) 
						: hmsToHHMMF(first) 
							+ (second>=0 && second!=first ? "(" + hmsToHHMMF(second) + ")" : "");
	}
	
	public String getDepartureTimes() { return "xx:xx"; }
	
	public String getArrivalTimes() { return "xx:xx"; }
	
	public String getPlatform() { return ""; }
	
	public String getPathingAndAllowances() { return ""; }
	
	public String getSummary() {
		return String.format("%-13s %-30s %3s %-13s",
							getArrivalTimes(), getLocation(), getPlatform(), 
							getDepartureTimes(), getPathingAndAllowances());
	}
}

class RealPlanLocViewer extends PlanLocViewer {
	PlanLoc pl;
	public RealPlanLocViewer(PlanLoc pl) {
		this.pl = pl;
	}

	public String getLocation() { return LocViewer.get(pl.getLocation()).getDesc(); }
	public String getDepartureTimes() {
		return PlanLocViewer.firstBracketSecondOrBest(
				pl.getPubArrHMs(), pl.getDepHMs());
	}
	public String getArrivalTimes() {
		return PlanLocViewer.firstBracketSecondOrBest(pl.getPubArrHMs(), pl.getArrHMs());
	}
	
	public String getPlatform() { 
		String p = pl.getPlatform();
		return p==null ? "" : p; 
	}
	
	public String getPathingAndAllowances() {
		return pl.getLine()+", "+pl.getPath()+", "+
				PlanLocViewer.hmsToHHMMF(pl.getEngineeringHalfMins()) + ", " +
				PlanLocViewer.hmsToHHMMF(pl.getPathingAllowanceHalfMins()) + ", " +
				PlanLocViewer.hmsToHHMMF(pl.getPerformanceHalfMins());
	}
}
