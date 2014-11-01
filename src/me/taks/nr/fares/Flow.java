package me.taks.nr.fares;

public class Flow {
	public enum Status { ADULT };
	public enum Usage { ACTUAL, GROUPED, CONCAT_INTERNAL };
	public enum CrossLondon { NO, LU, NOT_LU, THAMESLINK };
	private int origin;
	private int dest;
	private int route;
	private Status status;
	private boolean reversible;
	private String startDate;
	private String endDate;
	private String toc;
	private CrossLondon crossLondon;
	private boolean privateSettlement;
	private boolean standardDiscounts;
	private boolean inNFM;
	private int id;
	
	public static class Fare {
		private int flowId;
		private String code;
		private int fare;
		private String restrictionCode;
	}
}
