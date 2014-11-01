package me.taks.nr.schedule;

import me.taks.nr.ReportViewer;

public class PlanViewer {
	public static PlanViewer get(Plan plan) {
		if (plan == null) return new PlanViewer();
		else return new RealPlanViewer(plan);
	}

	public String getHeadcode() { return "XXXX"; }
	
	public String getStartLocation() { return "Unknown Location"; }

	public String getFinishLocation() { return "Unknown Location"; }
	
	public String getStartTime() { return "xx:xx"; }
	
	public String getFinishTime() { return "xx:xx"; }
	
	public String getSummary() {
		return getHeadcode() + " " + getStartTime() + " " + getStartLocation() + " to " + 
				getFinishTime() + " " + getFinishLocation();
	}
	
	public String getDescription() { return ""; }
	
	public String getLocationList() {
		return "";
	}

	public String getSummaryPlusLocations() {
		return getSummary() + "\n" + getDescription() + "\n\n" + getLocationList();
	}
}

class RealPlanViewer extends PlanViewer {
	Plan p;
	public RealPlanViewer(Plan p) {
		this.p = p;
	}
	
	private PlanLocViewer start=null , end=null;
	
	private PlanLocViewer getStartViewer() {
		if (start==null) start = PlanLocViewer.get(p.getStartPlan());
		return start;
	}
	private PlanLocViewer getFinishViewer() {
		if (end==null) end = PlanLocViewer.get(p.getFinishPlan());
		return end;
	}
	
	public String getHeadcode() { 
		String hc = p.getHeadcode();
		return hc==null ? "XXXX" : hc; 
	}
	
	public String getRunningDays() {
		Schedule s = p.getSchedule();
		if (s==null) return "-------";
		StringBuffer out = new StringBuffer("MTWTFSS");
		short days = s.getDays();
		for (int i=0; i<7; i++)
			if ((days & (1<<i)) == 0) out.replace(i, i+1, "-");
		return out.toString();
	}
	
	public String getStartLocation() { return getStartViewer().getLocation(); }
	public String getFinishLocation() { return getFinishViewer().getLocation(); }
	
	public String getStartTime() {
		return getStartViewer().getDepartureTimes();
	}
	public String getFinishTime() {
		return getFinishViewer().getArrivalTimes();
	}

	public String getStart() {
		long s = p.getSchedule().getStart();
		return String.format("%td-%tb", s, s);
	}
	
	public String getEnd() {
		long s = p.getSchedule().getEnd();
		return String.format("%td-%tb", s, s);
	}
	
	public String getDescription() { 
		return getRunningDays() + " " + getStart() + " to " + getEnd() + "\n" + p.getSpeed() + "mph "+
				(p.getEmuClass()>0 ? "class " + p.getEmuClass()+" " : "")+
				p.getPower().toString().replace('_', ' ').replace(" OTHER", "")+ " " +
				(!p.isHasFirstClass()?" Std Only":"")+ " " +
				ReportViewer.titleCase(p.getCategory().toString().replace('_', ' ').replace(" OTHER", "").replace("ORDINARY ", "")); 
	}
	
	public String getLocationList() {
		StringBuffer out = new StringBuffer();
		int len = p.getPlanLocCount();
		int rows = len<6 ? len : (int)Math.ceil(len/2);
		for (int i=0; i<rows; i+=(len<6?1:2)) {
			out.append(String.format("%-65s", PlanLocViewer.get(p.getPlanLoc(i)).getSummary()));
			if (len>=6) {
				out.append(PlanLocViewer.get(p.getPlanLoc(rows+i)).getSummary());
			}
			out.append("\n");
		}
		out.append("----------------------------------------------------------------------------------\n");
		return out.toString();
	}

}
