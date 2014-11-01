package me.taks.nr.subs;

import me.taks.json.JSONArray;
import me.taks.json.JSONObject;
import me.taks.nr.Report;
import me.taks.nr.ReportViewer;

public class Subscription {
	public enum Type { android }
	private Type type;
	private String clientId;
	private String filter; //obviously this should be stored parsed
	
	
	public Type getType() {
		return type;
	}

	public String getClientId() {
		return clientId;
	}

	public String getFilter() {
		return filter;
	}

	public Subscription(Type type, String clientId, String filter) {
		this.type = type;
		this.clientId = clientId;
		this.filter = filter;
	}
	
	public String toJSONString() {
		return "["+type+","+JSONObject.valueToString(clientId)+","+JSONObject.valueToString(filter)+"]";
	}
	
	public static Subscription fromJSON(String json) {
		return fromJSON(new JSONArray(json));
	}
	public static Subscription fromJSON(JSONArray a) {
		return new Subscription(Type.valueOf(a.getString(0)), a.getString(1), a.getString(2));
	}
	
	public boolean matches(Report report) {
		String[] rule = filter.split("=");
		if (rule.length<2) return false;
		else if ("loc".equals(rule[0]) && report.getLocation()!=null 
										&& rule[1].equals(report.getLocation().getStanox()))
			return true;
		else if ("headcode".equals(rule[0]) && rule[1].equals(ReportViewer.get(report).getHeadcode()))
			return true;
		else if ("trainId".equals(rule[0]) && rule[1].equals(report.getTrainId()))
			return true;
		else return false;
	}
}
