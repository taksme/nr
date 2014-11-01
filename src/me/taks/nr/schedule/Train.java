package me.taks.nr.schedule;

import java.util.ArrayList;

import me.taks.nr.data.Schedule.Plan;

public class Train {
	public String id;
	public String headcode;
	public ArrayList<Plan> plans = new ArrayList<Plan>();
	public ArrayList<Relation> assocs = new ArrayList<Relation>();
	
	public Train(Plan plan) {
		id = plan.getId();
		headcode = plan.getHeadcode();
		plans.add(plan);
	}
	
	public void add(Plan plan) {
		plans.add(plan);
	}
	
	public String toJsonArray() {
		StringBuffer out = new StringBuffer();
		
		return out.toString();
	}
	
	public String getId() {
		return id;
	}
	
	public String getHeadcode() {
		return headcode;
	}
}
